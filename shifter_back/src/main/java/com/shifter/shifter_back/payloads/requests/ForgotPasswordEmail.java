package com.shifter.shifter_back.payloads.requests;

import com.shifter.shifter_back.annotations.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ForgotPasswordEmail extends EmailBase {
    private String url;

}


