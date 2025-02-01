package com.shifter.shifter_back.payloads.responses;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Data
@Builder
public class DataResponse {
    private List<?> data;
    private Pageable pageable;
}
