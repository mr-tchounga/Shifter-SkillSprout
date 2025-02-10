package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.RefreshToken;
import com.shifter.shifter_back.payloads.responses.UserMachineDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface RefreshTokenService {
    public Optional<RefreshToken> findByToken(String token);
    public RefreshToken createRefreshToken(Long userId, UserMachineDetails userMachineDetails);
    public RefreshToken verifyExpiration(RefreshToken token);
    public void deleteByUserId(Long userId);
}
