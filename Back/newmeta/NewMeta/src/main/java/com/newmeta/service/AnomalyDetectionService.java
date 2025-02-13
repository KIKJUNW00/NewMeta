package com.newmeta.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.PredictionResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectionService {

    private final FastAPIService fastAPIService;
    private final AnomalyLogService anomalyLogService;
    
    private static final int DOMESTIC_AI_LIMIT = 10;
    private static final int IMPORTED_AI_LIMIT = 8;
    private static final String DOMESTIC_PREFIX = "001.880";

    /**
     * 🚀 이상 탐지 메인 메서드
     */
    public void detectAnomalies(String epcCode, List<ProductEventLog> events) {
        log.info("[AnomalyDetectionService] 이상 탐지 시작: EPC={}, 이벤트 수={}", epcCode, events.size());

        // 1️⃣ 국내산 첫 이벤트 검사
        Optional<ProductEventLog> domesticIssue = findDomesticFirstEventIssue(epcCode, events);
        if (domesticIssue.isPresent()) {
            anomalyLogService.saveAnomalyLog(domesticIssue.get(), "위조", "commissioning 이후 첫 이벤트가 올바르지 않음");
            return;
        }

        // 2️⃣ 수입산 첫 이벤트 검사
        Optional<ProductEventLog> importedIssue = findImportedFirstEventIssue(epcCode, events);
        if (importedIssue.isPresent()) {
            anomalyLogService.saveAnomalyLog(importedIssue.get(), "밀수", "custom_inbound 이후 첫 이벤트가 잘못됨");
            return;
        }

        // 3️⃣ 직접 판매 이벤트 검사 (허브 없이 판매)
        Optional<ProductEventLog> directSellEvent = findDirectSellEvent(events);
        if (directSellEvent.isPresent()) {
            anomalyLogService.saveAnomalyLog(directSellEvent.get(), "불법 유통", "허브 이동 없이 판매 발생");
            return;
        }

        // 4️⃣ 허가되지 않은 이벤트 검사
        Optional<ProductEventLog> unauthorizedEvent = findUnauthorizedEvent(events);
        if (unauthorizedEvent.isPresent()) {
            anomalyLogService.saveAnomalyLog(unauthorizedEvent.get(), "이상 이벤트 발생", "허용되지 않은 이벤트 탐지됨");
            return;
        }

        // 5️⃣ 이벤트 순서 오류 검사
        Optional<ProductEventLog> sequenceErrorEvent = findSequenceErrorEvent(epcCode, events);
        if (sequenceErrorEvent.isPresent()) {
            anomalyLogService.saveAnomalyLog(sequenceErrorEvent.get(), "이벤트 순서 오류", "정상 흐름 불일치");
            return;
        }

        // 6️⃣ AI 호출 및 이상 탐지
        callAIAndProcessResults(epcCode, events);

        // 7️⃣ 이상 없으면 모든 이벤트를 정상 처리로 표시
        markAllAsNormal(events, epcCode);
    }

    /**
     * ✅ AI 호출 및 결과 처리
     */
    private void callAIAndProcessResults(String epcCode, List<ProductEventLog> events) {
        boolean isDomestic = isDomesticProduct(epcCode);
        int aiLimit = isDomestic ? DOMESTIC_AI_LIMIT : IMPORTED_AI_LIMIT;
        
        if (events.size() >= aiLimit) {
            try {
                List<PredictionResultDTO> aiResults = fastAPIService.detectAnomalies(events);
                if (!aiResults.isEmpty()) {
                    for (PredictionResultDTO result : aiResults) {
                        Optional<ProductEventLog> matchingEvent = events.stream()
                                .filter(e -> e.getProduct().getEpcCode().equals(result.getEpcCode()))
                                .findFirst();
                        if (matchingEvent.isPresent() && result.isAnomaly()) {
                            anomalyLogService.saveAnomalyLog(matchingEvent.get(), "AI 기반 이상 탐지", "LSTM 모델 이상 패턴 감지");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("❌ AI 호출 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * ✅ 모든 이벤트를 정상 처리로 표시
     */
    private void markAllAsNormal(List<ProductEventLog> events, String epcCode) {
        for (ProductEventLog event : events) {
            event.setIsAnomaly(false);
        }
        log.info("[정상 이벤트 처리 완료] EPC={}", epcCode);
    }

    // -------------------------------------------------------------------------
    // 🔍 이상 탐지 메서드들
    // -------------------------------------------------------------------------
    private boolean isDomesticProduct(String epcCode) {
        return epcCode != null && epcCode.startsWith(DOMESTIC_PREFIX);
    }

    private Optional<ProductEventLog> findDomesticFirstEventIssue(String epcCode, List<ProductEventLog> events) {
        if (!isDomesticProduct(epcCode) || events.size() < 2) return Optional.empty();
        ProductEventLog secondEvent = events.get(1);
        if (!List.of("aggregation", "WMS_inbound").contains(secondEvent.getEvent().getEventType())) {
            return Optional.of(secondEvent);
        }
        return Optional.empty();
    }

    private Optional<ProductEventLog> findImportedFirstEventIssue(String epcCode, List<ProductEventLog> events) {
        if (isDomesticProduct(epcCode) || events.size() < 2) return Optional.empty();
        ProductEventLog secondEvent = events.get(1);
        if (!"custom_outbound".equals(secondEvent.getEvent().getEventType())) {
            return Optional.of(secondEvent);
        }
        return Optional.empty();
    }

    private Optional<ProductEventLog> findDirectSellEvent(List<ProductEventLog> events) {
        boolean hasHubInbound = events.stream().anyMatch(e -> "stock_inbound(HUB)".equals(e.getEvent().getEventType()));
        if (!hasHubInbound) {
            return events.stream().filter(e -> "stock_outbound(Sell)".equals(e.getEvent().getEventType())).findFirst();
        }
        return Optional.empty();
    }

    private Optional<ProductEventLog> findUnauthorizedEvent(List<ProductEventLog> events) {
        List<String> unauthorizedEvents = List.of("illegal_transfer", "fake_aggregation", "unauthorized_custom");
        return events.stream().filter(e -> unauthorizedEvents.contains(e.getEvent().getEventType())).findFirst();
    }

    private Optional<ProductEventLog> findSequenceErrorEvent(String epcCode, List<ProductEventLog> events) {
        boolean isDomestic = isDomesticProduct(epcCode);
        List<String> flow = isDomestic ? getDomesticEventFlow() : getImportedEventFlow();
        int idx = 0;
        
        for (ProductEventLog evt : events) {
            String eventType = evt.getEvent().getEventType();
            if (idx < flow.size() && eventType.equals(flow.get(idx))) {
                idx++;
            } else {
                return Optional.of(evt);
            }
        }
        return Optional.empty();
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
