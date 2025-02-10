package com.newmeta.service; // 패키지 선언

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newmeta.domain.ProductEventLog; // ProductEventLog 클래스 임포트
import com.newmeta.domain.dto.PredictionResultDTO; // PredictionResultDTO 클래스 임포트
import lombok.extern.slf4j.Slf4j; // 로깅을 위한 SLF4J 어노테이션 임포트
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션 임포트
import org.springframework.http.HttpHeaders; // HTTP 헤더 처리를 위한 클래스 임포트
import org.springframework.http.MediaType; // 미디어 타입 처리를 위한 클래스 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답 처리를 위한 클래스 임포트
import org.springframework.stereotype.Service; // 서비스 클래스를 정의하기 위한 어노테이션 임포트
import org.springframework.web.reactive.function.client.WebClient; // 비동기 웹 클라이언트를 위한 클래스 임포트
import org.springframework.web.reactive.function.client.WebClientResponseException; // 웹 클라이언트 응답 예외 처리를 위한 클래스 임포트

import java.text.SimpleDateFormat; // 날짜 포맷 처리를 위한 클래스 임포트
import java.util.*; // 유틸리티 클래스 임포트 (List, Map 등)
import java.util.stream.Collectors; // 스트림 API를 위한 클래스 임포트

@Service // 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
@Slf4j // SLF4J 로깅 기능을 사용하기 위한 어노테이션
public class FastAPIService {

    @Autowired
    private WebClient webClient; // WebClient 인스턴스 선언

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 날짜 포맷 설정
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper

    public List<PredictionResultDTO> detectAnomalies(List<ProductEventLog> events) {
        try {
            // 유효한 이벤트 필터링
            List<ProductEventLog> validEvents = events.stream()
                    .filter(event -> event.getProduct() != null && event.getEventTime() != null && event.getEvent().getEventType() != null)
                    .collect(Collectors.toList());

            if (validEvents.isEmpty()) {
                log.warn("⚠️ 유효한 이벤트가 없습니다.");
                return Collections.emptyList();
            }

            // FastAPI에 보낼 데이터 생성
            List<Map<String, Object>> requestData = validEvents.stream()
                    .map(this::convertToFastAPIInput)
                    .collect(Collectors.toList());

            log.info("전송 데이터: {}", requestData);

            // FastAPI에 POST 요청 보내기 (ResponseEntity<String> 형태로 응답 받음)
            ResponseEntity<String> responseEntity = webClient.post()
                    .uri("/detect_anomaly")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestData)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            // 응답 상태 코드 및 본문 로깅
            log.info("📡 FastAPI 응답 상태: {}", responseEntity.getStatusCode());
            log.info("📡 FastAPI 응답 본문: {}", responseEntity.getBody());

            // JSON 문자열을 Map으로 변환
            Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {});

            // "results" 키의 값을 List<Map<String, Object>>로 변환
            @SuppressWarnings("unchecked")
			List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseMap.get("results");

            if (resultList == null || resultList.isEmpty()) {
                log.warn("⚠️ FastAPI 응답이 비어있습니다.");
                return Collections.emptyList();
            }

            // Map 리스트를 PredictionResultDTO 객체 리스트로 변환
            List<PredictionResultDTO> responseList = resultList.stream()
                    .map(map -> objectMapper.convertValue(map, PredictionResultDTO.class))
                    .collect(Collectors.toList());

            // 응답 결과 로깅
            responseList.forEach(result -> {
                if (result.getEpcCode() == null) {
                    log.warn("⚠️ FastAPI 응답에서 EPC 코드가 null입니다. 응답 내용: {}", result);
                } else {
                    log.info("📡 FastAPI 응답: EPC={}, IsAnomaly={}", result.getEpcCode(), result.isAnomaly());
                }
            });

            return responseList;

        } catch (WebClientResponseException e) {
            handleWebClientException(e);
        } catch (Exception e) {
            log.error("❌ FastAPI 호출 실패: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private Map<String, Object> convertToFastAPIInput(ProductEventLog event) {
        Map<String, Object> data = new HashMap<>();
        data.put("epc_code", event.getProduct().getEpcCode());
        data.put("product_serial", String.valueOf(event.getProduct().getProductSerial()));
        data.put("product_name", event.getProduct().getProductName());
        data.put("hub_type", event.getHub().getHubType());
        data.put("event_type", event.getEvent().getEventType());
        data.put("event_time", dateFormat.format(event.getEventTime()));
        return data;
    }

    private void handleWebClientException(WebClientResponseException e) {
        if (e.getStatusCode().is4xxClientError()) {
            log.error("❌ 클라이언트 오류: 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString());
        } else if (e.getStatusCode().is5xxServerError()) {
            log.error("❌ 서버 오류: 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString());
        } else {
            log.error("❌ WebClient 오류: 상태 코드={}, 응답={}", e.getStatusCode(), e.getResponseBodyAsString());
        }
    }
}

