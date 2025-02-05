package com.newmeta.domain.dto; // 해당 DTO 클래스가 속한 패키지를 선언

import java.util.Date; // 날짜 및 시간을 표현하기 위해 Java의 Date 클래스를 임포트

import lombok.AllArgsConstructor; // 모든 필드를 포함하는 생성자를 자동으로 생성하는 Lombok 어노테이션
import lombok.Builder; // 객체 생성 시 빌더 패턴을 지원하는 Lombok 어노테이션
import lombok.Data; // Getter, Setter, toString, equals, hashCode 메서드를 자동 생성하는 Lombok 어노테이션
import lombok.NoArgsConstructor; // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션

/**
 * ProductEventLogDTO 클래스는 제품 이벤트 로그 정보를 담는 데이터 전송 객체(DTO)
 * 클라이언트와 서버 간 데이터 교환을 위해 사용됨
 */
@Data // 클래스 내 모든 필드에 대해 Getter, Setter, toString(), equals(), hashCode() 등을 자동 생성
@NoArgsConstructor // 기본 생성자를 자동으로 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자를 자동으로 생성
@Builder // 빌더 패턴을 적용하여 객체 생성 시 유연성을 제공
public class ProductEventLogDTO {

    private Long productEventLogId; // 이벤트 로그의 고유 식별자 (Primary Key 역할)

    private Date eventTime; // 이벤트가 발생한 날짜 및 시간 정보

    private String epcCode; // EPC(Electronic Product Code), 제품의 고유 식별 코드

    private String productName; // 제품의 이름을 저장하는 필드

    private String eventType; // 이벤트의 유형 (예: commissioning, shipping 등)

    private String hubName; // 이벤트가 발생한 허브(물류센터)의 이름
    
    private Double latitude; // 이벤트가 발생한 위치의 위도 정보

    private Double longitude; // 이벤트가 발생한 위치의 경도 정보

    private boolean anomaly; // 이벤트가 정상적인지 여부를 나타내는 필드 (이상 탐지 여부)
    
    // ✅ 오류 해결: DTO 생성자 추가
    public ProductEventLogDTO(String epcCode, String eventType, String hubName, Date eventTime, Double latitude, Double longitude) {
        this.epcCode = epcCode;
        this.eventType = eventType;
        this.hubName = hubName;
        this.eventTime = eventTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
