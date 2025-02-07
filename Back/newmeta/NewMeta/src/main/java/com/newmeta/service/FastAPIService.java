package com.newmeta.service;

import com.newmeta.domain.ProductEventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 📌 AI(FastAPI) 연동 서비스
 * - 여러 이벤트를 POST로 전달 -> 이상 판정(스코어) 수신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FastAPIService {

    // 개선사항: RestTemplate 대신 WebClient(비동기), fastApiUrl을 @Value로 주입
    private final RestTemplate restTemplate = new RestTemplate();

    private final String fastApiUrl = "http://localhost:8000/detect_anomaly";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 📌 FastAPI에 이상 탐지 요청
     * - 개선사항: 모델 응답 형식(스코어, bool 등)에 따라 처리 달라짐
     */
    public List<Map<String, Object>> detectAnomalies(List<ProductEventLog> events) {
        try {
            // JSON 변환
            List<Map<String, Object>> requestData = events.stream()
                    .map(this::convertToFastAPIInput)
                    .collect(Collectors.toList());

            // HTTP Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Request
            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(requestData, headers);

            // POST
            ResponseEntity<List> response = restTemplate.exchange(
                    fastApiUrl, HttpMethod.POST, request, List.class);

            if (response.getBody() == null) {
                log.warn("⚠️ FastAPI 응답이 null, 빈 리스트 반환");
                return Collections.emptyList();
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("❌ FastAPI 호출 실패: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 📌 ProductEventLog -> FastAPI Input 변환
     */
    private Map<String, Object> convertToFastAPIInput(ProductEventLog event) {
        Map<String, Object> data = new HashMap<>();
        data.put("epc_code", event.getProduct().getEpcCode());
        data.put("product_serial", String.valueOf(event.getProduct().getProductSerial()));
        data.put("product_name", event.getProduct().getProductName());
        data.put("hub_type", event.getHub().getHubName());
        data.put("event_type", event.getEvent().getEventType());
        data.put("event_time", dateFormat.format(event.getEventTime()));
        return data;
    }
}
