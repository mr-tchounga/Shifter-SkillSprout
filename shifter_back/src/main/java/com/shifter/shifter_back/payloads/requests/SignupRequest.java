package com.shifter.shifter_back.payloads.requests;

import com.shifter.shifter_back.annotations.UniqueEmail;
import com.shifter.shifter_back.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @UniqueEmail
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Name must not be blank")
    @NotNull(message = "Invalid Name: Name is NULL")
    @Size(min = 3, message = "Name must have at least 3 characters")
    private String name;

    @ValidPassword
    private String password;

}
