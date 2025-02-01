package com.shifter.shifter_back.repositories.auth;

import com.shifter.shifter_back.models.auth.PasswordResetToken;
import com.shifter.shifter_back.models.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    Optional<PasswordResetToken> findByToken(String token);
}
