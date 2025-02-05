package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.exceptions.BadRequestException;
import com.shifter.shifter_back.exceptions.ResourceNotFoundException;
import com.shifter.shifter_back.exceptions.TokenRefreshException;
import com.shifter.shifter_back.exceptions.UserAlreadyExistException;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.PasswordResetToken;
import com.shifter.shifter_back.models.auth.RefreshToken;
import com.shifter.shifter_back.payloads.requests.*;
import com.shifter.shifter_back.payloads.responses.JwtAuthenticationResponse;
import com.shifter.shifter_back.payloads.responses.UserMachineDetails;
import com.shifter.shifter_back.repositories.auth.UserRepository;
import com.shifter.shifter_back.repositories.auth.PasswordTokenRepository;
import com.shifter.shifter_back.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class authServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordTokenRepository passwordTokenRepository;

    @Override
    public JwtAuthenticationResponse signUp(SignupRequest request, UserMachineDetails userMachineDetails) {
        // Log the request data
        System.out.println("Received signup request for: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistException(request.getEmail());
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(true)
                .build();
        user = userRepository.save(user);

        System.out.println("Creating user: " + user.getEmail());

        // Continue with authentication
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        System.out.println("authToken : " + usernamePasswordAuthenticationToken);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // Log machine details
        System.out.println("Machine details: " + userMachineDetails.getOperatingSystem());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("user: " + authentication.getPrincipal());

        String jwt = jwtUtils.generateJwtToken((User) authentication.getPrincipal());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), userMachineDetails);

        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .user(user)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }


    @Override
    public JwtAuthenticationResponse signIn(SigninRequest request, UserMachineDetails userMachineDetails) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken((User) authentication.getPrincipal());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), userMachineDetails);
        return JwtAuthenticationResponse.builder().token(jwt).user(user).refreshToken(refreshToken.getToken()).build();
    }

    @Override
    public JwtAuthenticationResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map((user) -> {
                    String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                    return JwtAuthenticationResponse.builder().token(token).user(user).refreshToken(requestRefreshToken).build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database"));
    }

    @Override
    public void logout(User user) {
        refreshTokenService.deleteByUserId(user.getId());
    }

    @Override
    public PasswordResetToken forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        String token = UUID.randomUUID().toString();
        return passwordTokenRepository.save(PasswordResetToken.builder()
                .token(token).user(user.get())
                .expiryDate(LocalDateTime.now().plusMinutes(PasswordResetToken.EXPIRATION))
                .build());
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }
    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDateTime.now());
    }
    @Override
    public boolean isResetTokenValid(String token) {
        System.out.println(token);
        Optional<PasswordResetToken> passToken = passwordTokenRepository.findByToken(token);
        if (passToken.isEmpty()) {
            throw new ResourceNotFoundException("Token not found");
        }

//        return isTokenFound(passToken.get()) && (!isTokenExpired(passToken.get()));
        return !isTokenExpired(passToken.get());
    }

    @Override
    public User resetPassword(ForgotPassword forgotPassword) {
        User user =  passwordTokenRepository.findByToken(forgotPassword.getToken()).get().getUser();
        user.setPassword(passwordEncoder.encode(forgotPassword.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(PasswordReset passwordReset, User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), passwordReset.getPassword()));
        user.setPassword(passwordEncoder.encode(passwordReset.getPassword()));
        User updatedUser = userRepository.save(user);
        this.logout(user);
        return updatedUser;
    }

    @Override
    public User getCurrentUserFromToken(String token) {
        String email = jwtUtils.getEmailFromJwtToken(token);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        return user.get();
    }
}
