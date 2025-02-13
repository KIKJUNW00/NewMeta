package com.newmeta.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class PredictionResultDTO {
	
	@JsonProperty("epc_code")  // JSON의 epc_code 키와 매핑
    private String epcCode;

    @JsonProperty("is_anomaly")  // JSON의 is_anomaly 키와 매핑
    private boolean isAnomaly;
}