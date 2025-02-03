package com.newmeta.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Hub {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 설정
	private Long hubId;
	private String hubName;
	private Double latitude; // 위도
	private Double longitude; // 경도

	
	// ✅ [추가] String, double, double을 지원하는 생성자 추가
    public Hub(String hubName, Double latitude, Double longitude) {
        this.hubName = hubName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
