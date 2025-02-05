package com.newmeta.service;

import com.newmeta.cache.EventHistoryCache;
import com.newmeta.domain.AnomalyLog;
import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.AnomalyDTO;
import com.newmeta.persistence.AnomalyLogRepository;
import com.newmeta.persistence.ProductEventLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectionService {

    private final AnomalyLogRepository anomalyLogRepository;
    private final ProductEventLogRepository productEventLogRepository;
    private final WebSocketService webSocketService;
    private final EventHistoryCache eventHistoryCache;
    // private final FastAPIService fastAPIService; // ✅ FastAPI 호출 기능 주석 처리

    /**
     * 🚀 제품 이벤트 저장 및 이상 탐지 수행
     */
    public void processEvent(ProductEventLog eventLog) {
        log.info("📌 [processEvent 호출] eventLog={}", eventLog);

        if (eventLog == null || eventLog.getProduct() == null) {
            log.warn("❗ [데이터 누락] 이벤트 데이터가 NULL 입니다.");
            return;
        }

        String epcCode = eventLog.getProduct().getEpcCode();
        boolean isDomestic = isDomesticProduct(epcCode);

        log.info("✅ [EPC 코드 확인] EPC={} | isDomestic={}", epcCode, isDomestic);

        // **이벤트 기본값을 정상(`false`)으로 설정**
        eventLog.setIsAnomaly(false);
        productEventLogRepository.save(eventLog);

        // 이벤트 히스토리 저장
        eventHistoryCache.addEvent(eventLog);
        List<ProductEventLog> storedEvents = eventHistoryCache.getEventsByEpc(epcCode);

        // 필수 이벤트 리스트 설정
        List<String> requiredEvents = isDomestic ? getDomesticEventFlow() : getImportedEventFlow();

        log.info("✅ [필수 이벤트 확인] EPC={} | 저장된 이벤트 개수={} | 필요한 이벤트 개수={}",
                 epcCode, storedEvents.size(), requiredEvents.size());

        // 필수 이벤트가 모두 충족되었는지 확인
        boolean allEventsPresent = requiredEvents.stream()
            .allMatch(reqEvent -> storedEvents.stream()
                .anyMatch(e -> e.getEvent().getEventType().equals(reqEvent)));

        if (allEventsPresent) {
            log.info("🚀 [이상 탐지 실행] EPC={} | 모든 필수 이벤트 수집 완료", epcCode);
            detectAnomalies(epcCode, storedEvents, requiredEvents);
        } else {
            log.warn("⚠️ [이상 탐지 미실행] EPC={} | 필수 이벤트가 모두 모이지 않음", epcCode);
        }
    }

    /**
     * 🚀 이상 탐지 (FastAPI 호출 제외)
     */
    private void detectAnomalies(String epcCode, List<ProductEventLog> events, List<String> requiredEvents) {
        log.info("🚀 [detectAnomalies 호출] EPC={} | 이벤트 개수={}", epcCode, events.size());

        // 필수 이벤트 누락 여부 확인
        for (String requiredEvent : requiredEvents) {
            boolean eventExists = events.stream()
                .anyMatch(e -> e.getEvent().getEventType().equals(requiredEvent));

            if (!eventExists) {
                log.warn("⚠️ [이벤트 누락 감지] EPC={} | 누락된 이벤트={}", epcCode, requiredEvent);
                saveAnomalyLog(events.get(0), "이벤트 순서 오류", "필수 이벤트 [" + requiredEvent + "] 누락");
                return;
            }
        }

        // ✅ FastAPI 호출 부분 주석 처리
        // boolean isAnomalous = fastAPIService.isAnomalous(events);
        log.info("📌 [FastAPI 호출 없이 검증 진행] EPC={}", epcCode);

        boolean isAnomalous = false; // FastAPI 호출 없이 기본값 설정

        if (isAnomalous) {
            saveAnomalyLog(events.get(0), "AI 기반 이상 탐지", "LSTM 모델이 이상 패턴 감지");
        } else {
            log.info("✅ [정상 이벤트] EPC={} | FastAPI 호출 없이 정상 처리", epcCode);
            for (ProductEventLog event : events) {
                event.setIsAnomaly(false);
                productEventLogRepository.save(event);
            }
        }

        log.info("✅ [정상 이벤트 저장 완료] EPC={}", epcCode);
        eventHistoryCache.removeEventHistory(epcCode);
    }

    /**
     * ✅ anomaly_log 저장 + WebSocket 실시간 알림 추가
     */
    private void saveAnomalyLog(ProductEventLog eventLog, String anomalyType, String reason) {
        log.info("📌 [saveAnomalyLog 호출] eventLog={} | anomalyType={} | reason={}", eventLog, anomalyType, reason);

        if (eventLog == null || eventLog.getProduct() == null || eventLog.getEvent() == null || eventLog.getHub() == null) {
            log.warn("❗ [이상 탐지 저장 실패] NULL 값 존재 - anomalyLog 생성 중단");
            return;
        }

        String epcCode = eventLog.getProduct().getEpcCode();
        log.info("🚨 [이상 탐지 발생] EPC={} | 유형={} | 이유={}", epcCode, anomalyType, reason);

        eventLog.setIsAnomaly(true);
        productEventLogRepository.save(eventLog);

        AnomalyLog anomalyLog = AnomalyLog.builder()
                .anomalyType(anomalyType)
                .reason(reason)
                .epcCode(epcCode)
                .anomalyTimestamp(eventLog.getEventTime())
                .anomalyEventType(eventLog.getEvent().getEventType())
                .anomalyHub(eventLog.getHub().getHubName())
                .anomalyProductName(eventLog.getProduct().getProductName())
                .latitude(eventLog.getHub().getLatitude())
                .longitude(eventLog.getHub().getLongitude())
                .productEventLog(eventLog)
                .build();

        anomalyLogRepository.save(anomalyLog);
        log.info("✅ [이상 로그 저장 완료] EPC={}", epcCode);

        AnomalyDTO anomalyAlert = AnomalyDTO.builder()
                .anomalyType(anomalyLog.getAnomalyType())
                .reason(anomalyLog.getReason())
                .epcCode(anomalyLog.getEpcCode())
                .build();

        webSocketService.sendAnomalyAlert(Collections.singletonList(anomalyAlert));

        log.info("🚨 [WebSocket 알림 전송 완료] EPC={} | reason={}", epcCode, reason);
    }

    private boolean isDomesticProduct(String epcCode) {
        return epcCode.startsWith("001.880");
    }

    private List<String> getDomesticEventFlow() {
        return List.of("commissioning", "aggregation", "WMS_inbound", "WMS_outbound",
                       "stock_inbound(HUB)", "stock_outbound(HUB)", "stock_inbound(Wholesaler)",
                       "stock_outbound(Wholesaler)", "stock_inbound(Reseller)", "stock_outbound(Sell)");
    }

    private List<String> getImportedEventFlow() {
        return List.of("custom_inbound", "custom_outbound", "stock_inbound(HUB)", "stock_outbound(HUB)",
                       "stock_inbound(Wholesaler)", "stock_outbound(Wholesaler)", "stock_inbound(Reseller)",
                       "stock_outbound(Sell)");
    }
}
