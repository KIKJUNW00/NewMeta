package com.newmeta.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📌 AnomalyDTO 클래스
 * ✅ 이상 탐지 데이터를 저장하고 전송하는 DTO
 */
@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor  // ✅ 이미 모든 필드를 포함한 생성자를 자동으로 생성함 (중복 생성자 필요 없음)
public class AnomalyDTO {


    private Long anomalyId;
    private String anomalyType;
    private String reason;
    
    private String epcCode;
    
    @JsonProperty("anomalyProductName")
    private String productName;        
    
    @JsonProperty("anomalyEventType")
    private String eventType;          
    
    @JsonProperty("anomalyHub")
    private String hubType;            
    private Double latitude;
    private Double longitude;
    private Date anomalyTimestamp;
}
