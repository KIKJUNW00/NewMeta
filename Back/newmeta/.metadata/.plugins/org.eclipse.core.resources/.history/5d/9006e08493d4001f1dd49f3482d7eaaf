package com.newmeta.domain;


import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
public class Meta {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq; // 기본키
	private Long productSerial; // 제품 일련번호
	private String epcCode; // EPC 코드
	private String productName; // 제품명
	private String hubType; // 물류 거점 유형
	private String eventType; // 이벤트 유형
	@Temporal(TemporalType.DATE) 
	private Date eventTime; // 이벤트 발생
	private Double latitude; // 위도
	private Double longitude; // 경도
	private boolean cfStatus; // 위변조 여부
	private boolean idStatus; // 불법유통 여부

}
