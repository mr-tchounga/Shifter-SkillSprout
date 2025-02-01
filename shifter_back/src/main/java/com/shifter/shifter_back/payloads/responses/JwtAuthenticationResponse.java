package com.shifter.shifter_back.payloads.responses;

import com.shifter.shifter_back.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthenticationResponse {
    @Schema(description = "Current user")
    private User user;
    @Schema(description = "New token generated")
    private String token;
    @Schema(description = "Token to generate other access tokens")
    private String refreshToken;
    @Schema(description = "Type of token")
    private String tokenType;
}
