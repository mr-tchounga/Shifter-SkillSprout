package com.shifter.shifter_back.payloads.requests;

import com.shifter.shifter_back.annotations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordReset {
    @NotBlank(message = "old password must not be blank")
    private String oldPassword;
    @ValidPassword
    private String password;
}
