package com.shifter.shifter_back.controllers;

import com.shifter.shifter_back.exceptions.BadRequestException;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.PasswordResetToken;
import com.shifter.shifter_back.payloads.requests.*;
import com.shifter.shifter_back.payloads.responses.JwtAuthenticationResponse;
import com.shifter.shifter_back.payloads.responses.UserMachineDetails;
import com.shifter.shifter_back.services.auth.AuthService;
import com.shifter.shifter_back.services.auth.EmailService;
import com.shifter.shifter_back.utils.HttpUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @Autowired
    private AuthService authenticationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Environment env;

    @Value("${backend.base.url}")
    private String baseUrl;

    @PostMapping("/signup")
    @Operation(summary = "User signup", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User Created"),
            @ApiResponse(responseCode = "400", description = "Email already exist"),
            @ApiResponse(responseCode = "500", description = "Server error"),
    })
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignupRequest signupRequest, HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
//        String ipAddress = HttpUtils.getClientIp();
        String ipAddress = HttpUtils.getClientIp(request);
        UserMachineDetails userMachineDetails = UserMachineDetails
                .builder()
                .ipAddress(ipAddress)
                .browser(userAgent.getBrowser().getName())
                .operatingSystem(userAgent.getOperatingSystem().getName())
                .build();
        return ResponseEntity.ok(authenticationService.signUp(signupRequest, userMachineDetails));
    }

    @PostMapping("/signin")
    @Operation(summary = "User signin", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "User not found or incorrect password"),
            @ApiResponse(responseCode = "500", description = "Server error"),
    })
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest signinRequest, HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
//        String ipAddress = HttpUtils.getClientIp();
        String ipAddress = HttpUtils.getClientIp(request);
        UserMachineDetails userMachineDetails = UserMachineDetails
                .builder()
                .ipAddress(ipAddress)
                .browser(userAgent.getBrowser().getName())
                .operatingSystem(userAgent.getOperatingSystem().getName())
                .build();
        return ResponseEntity.ok(authenticationService.signIn(signinRequest, userMachineDetails));
    }

    @PostMapping("/refresh")
    @Operation(summary = "User refresh token", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "403", description = "Refresh token doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Server error"),
    })
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send forgot password email", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "Email doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Server error"),
    })
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        PasswordResetToken password = authenticationService.forgotPassword(request.getEmail());
        ForgotPasswordEmail forgotPasswordEmail = ForgotPasswordEmail
                .builder().to(password.getUser().getEmail())
                .subject("Forgot password email")
                .url(env.getProperty("fronted.url") +  "/user/resetPassword?token=" + password.getToken())
                .baseUrl(baseUrl).build();
        Context context = new Context();
        context.setVariable("data", forgotPasswordEmail);
        emailService.sendEmailWithHtmlTemplate(forgotPasswordEmail, "emails/auth/forgot-password", context);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/reset-password/{token}")
    @Operation(summary = "Validate reset token", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Reset token doesn't exist or is expired"),
            @ApiResponse(responseCode = "500", description = "Server error"),
    })
    public ResponseEntity<Void> validateResetToken(@PathVariable String token) {
        final boolean valid = authenticationService.isResetTokenValid(token);
        if (valid) {
            return ResponseEntity.status((HttpStatus.NO_CONTENT)).build();
        } else {
            throw new BadRequestException("Reset token invalid");
        }
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "Reset user password", tags = "Auth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "500", description = "Server error"),
    })
    public ResponseEntity<User> resetPassword(@Valid @RequestBody  ForgotPassword forgotPassword) {
        final boolean valid = authenticationService.isResetTokenValid(forgotPassword.getToken());
        if (valid) {
            return ResponseEntity.ok((authenticationService.resetPassword(forgotPassword)));
        } else {
            throw new BadRequestException("Token invalid");
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", tags = "Auth")
    public User getCurrentUser(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        return authService.getCurrentUserFromToken(jwtToken);
    }

    @GetMapping("/signout")
    @Operation(summary = "User signout", tags = "Auth")
    public ResponseEntity<JwtAuthenticationResponse> signout(@RequestHeader("Authorization") String token) {
        authService.logout(getCurrentUser(token));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
