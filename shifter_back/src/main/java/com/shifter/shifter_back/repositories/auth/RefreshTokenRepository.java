package com.shifter.shifter_back.repositories.auth;

import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.RefreshToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);
    Page<RefreshToken> findAllByUser(Pageable pageable, User user);
    @Modifying
    void deleteByUser(User user);
}
