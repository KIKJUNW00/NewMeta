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
    private Long productEventLogId;
    private Date eventTime;
    private String epcCode;
    private String productName;
    private Long productSerial;  // 여기 추가 확인
    private String eventType;
    private String hubType;
    private Double latitude;
    private Double longitude;
    private Boolean isAnomaly;
    
}
