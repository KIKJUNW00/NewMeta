package com.newmeta.service; // 📌 해당 서비스 클래스가 속한 패키지를 선언

// ✅ Spring 관련 라이브러리 임포트
import com.newmeta.domain.ProductEventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 📌 **FastAPI 연동 서비스**
 * ✅ Spring Boot에서 FastAPI의 이상 탐지 모델을 호출하여 결과를 반환
 */
@Service // ✅ Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자를 Lombok이 자동 생성
@Slf4j // ✅ 로깅 기능을 자동으로 추가하는 Lombok 어노테이션
public class FastAPIService {

    // ✅ FastAPI와 HTTP 통신을 수행하는 RestTemplate 객체
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ FastAPI 이상 탐지 모델의 URL (로컬 환경 기준)
    private final String fastApiUrl = "http://localhost:8000/detect_anomaly";

    // ✅ 날짜 변환을 위한 포맷 설정
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 🚀 **이상 탐지 모델 호출 (FastAPI 연동)**
     * ✅ 특정 제품의 이벤트 데이터를 FastAPI로 전송하여 이상 탐지 여부 확인
     * @param events 제품의 이벤트 로그 목록
     * @return 이상 탐지 여부 (true: 이상, false: 정상)
     */
    public boolean isAnomalous(List<ProductEventLog> events) {
        try {
            // ✅ FastAPI에 전송할 데이터 변환 (JSON 형태로 구성)
            Map<String, Object> requestData = convertToFastAPIInput(events);

            // ✅ HTTP 요청 헤더 설정 (JSON)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ FastAPI 요청 엔티티 생성 (데이터 + 헤더)
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

            // ✅ FastAPI에 POST 요청을 보내고 응답을 받음
            ResponseEntity<Map> response = restTemplate.exchange(fastApiUrl, HttpMethod.POST, request, Map.class);

            // ✅ 응답 본문 확인
            if (response.getBody() == null || !response.getBody().containsKey("is_anomaly")) {
                log.warn("⚠️ FastAPI 응답 없음, 기본값 false 반환");
                return false;
            }

            return (boolean) response.getBody().get("is_anomaly");

        } catch (Exception e) {
            log.error("❌ FastAPI 이상 탐지 요청 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 🚀 **이벤트 로그 데이터를 FastAPI 입력 형식으로 변환**
     * ✅ FastAPI에서 처리할 수 있는 JSON 구조로 변환
     * @param events 제품 이벤트 로그 목록
     * @return FastAPI에 전달할 JSON 데이터 구조
     */
    private Map<String, Object> convertToFastAPIInput(List<ProductEventLog> events) {
        Map<String, Object> data = new HashMap<>();

        // ✅ 이벤트 데이터가 없는 경우 빈 객체 반환
        if (events.isEmpty()) return data;

        // ✅ 제품 정보 추가
        data.put("epc_code", events.get(0).getProduct().getEpcCode());
        data.put("product_name", events.get(0).getProduct().getProductName());

        // ✅ 이벤트 히스토리를 FastAPI에서 처리할 JSON 형태로 변환
        List<Map<String, Object>> eventList = events.stream().map(event -> {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("hub_type", event.getHub().getHubName());
            eventData.put("event_type", event.getEvent().getEventType());
            eventData.put("event_time", dateFormat.format(event.getEventTime())); // 날짜 변환
            eventData.put("latitude", event.getHub().getLatitude());
            eventData.put("longitude", event.getHub().getLongitude());
            return eventData;
        }).toList();

        // ✅ FastAPI에 전달할 데이터 구조 완성
        data.put("event_history", eventList);
        return data;
    }
}
