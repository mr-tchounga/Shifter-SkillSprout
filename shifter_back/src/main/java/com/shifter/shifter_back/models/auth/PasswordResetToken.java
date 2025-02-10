package com.shifter.shifter_back.models.auth;

import com.shifter.shifter_back.models.BaseEntity;
import com.shifter.shifter_back.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "password_reset_token")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken extends BaseEntity {

    public static final int EXPIRATION = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    @Schema(description = "Owner of token")
    @Column(name = "user_id")
    private User user;
    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
