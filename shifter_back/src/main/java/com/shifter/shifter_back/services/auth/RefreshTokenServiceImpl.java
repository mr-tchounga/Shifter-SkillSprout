package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.exceptions.TokenRefreshException;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.RefreshToken;
import com.shifter.shifter_back.payloads.responses.UserMachineDetails;
import com.shifter.shifter_back.repositories.auth.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${token.refresh.expiration}")
    private Long refreshTokenDurationMs = 0L;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    @Override
    public RefreshToken createRefreshToken(Long userId, UserMachineDetails userMachineDetails) {
        User user = userService.findById(userId);

        RefreshToken refreshToken = RefreshToken
                .builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .ipAddress(userMachineDetails.getIpAddress())
                .browser(userMachineDetails.getBrowser())
                .operationSystem(userMachineDetails.getOperatingSystem())
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUser(userService.findById(userId).getId());
    }
}
