package com.shifter.shifter_back.repositories.auth;

import com.shifter.shifter_back.models.auth.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);
    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId ORDER BY r.id DESC LIMIT 1")
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);
//    Page<RefreshToken> findAllByUser(Pageable pageable, Long userId);
    @Modifying
    @Transactional
//    @Query("DELETE FROM RefreshToken r WHERE r.userId = :user")
    @Query("UPDATE RefreshToken r " +
            "SET r.expiryDate = CURRENT_TIMESTAMP " +
            "WHERE r.userId = :user AND r.createdAt = (" +
                "SELECT r2.createdAt from RefreshToken r2 WHERE r2.userId = :user ORDER BY r2.id DESC LIMIT 1)")
    void deleteByUser(@Param("user") Long user);
}
