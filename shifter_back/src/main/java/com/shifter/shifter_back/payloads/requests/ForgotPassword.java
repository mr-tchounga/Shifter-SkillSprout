package com.shifter.shifter_back.payloads.requests;

import com.shifter.shifter_back.annotations.UniqueEmail;
import com.shifter.shifter_back.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassword {

    @ValidPassword
    private String password;
    @NotEmpty
    private String token;

}
