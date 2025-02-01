package com.shifter.shifter_back.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMachineDetails {
    private String browser;
    private String operatingSystem;
    private String ipAddress;
}
