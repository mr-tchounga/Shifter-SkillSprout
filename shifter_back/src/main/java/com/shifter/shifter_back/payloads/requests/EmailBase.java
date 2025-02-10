package com.shifter.shifter_back.payloads.requests;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmailBase {
    private String to;
    private String subject;
    private String baseUrl;

}
