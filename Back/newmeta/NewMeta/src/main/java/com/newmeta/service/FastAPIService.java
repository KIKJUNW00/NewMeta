package com.newmeta.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class FastAPIService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String fastApiUrl = "http://localhost:8000/detect_anomaly";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean isAnomalous(List<ProductEventLog> events) {
        try {
            Map<String, Object> requestData = convertToFastAPIInput(events);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

            ResponseEntity<Map> response = restTemplate.exchange(fastApiUrl, HttpMethod.POST, request, Map.class);

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

    private Map<String, Object> convertToFastAPIInput(List<ProductEventLog> events) {
        Map<String, Object> data = new HashMap<>();
        if (events.isEmpty()) return data;

        data.put("epc_code", events.get(0).getProduct().getEpcCode());
        data.put("product_name", events.get(0).getProduct().getProductName());

        List<Map<String, Object>> eventList = events.stream().map(event -> {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("hub_type", event.getHub().getHubName());
            eventData.put("event_type", event.getEvent().getEventType());
            eventData.put("event_time", dateFormat.format(event.getEventTime()));
            eventData.put("latitude", event.getHub().getLatitude());
            eventData.put("longitude", event.getHub().getLongitude());
            return eventData;
        }).toList();

        data.put("event_history", eventList);
        return data;
    }
}

