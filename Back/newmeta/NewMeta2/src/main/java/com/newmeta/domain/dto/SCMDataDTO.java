package com.newmeta.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SCMDataDTO {
    private String epcCode;
    private String eventType;
    private String hubName;
    private String eventTime;
}
