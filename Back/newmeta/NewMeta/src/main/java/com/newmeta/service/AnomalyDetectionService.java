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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 📌 이상 탐지 로직 서비스
 * - 새 이벤트 수신 시 processEvent → 캐시에 저장 → 필수 이벤트 충족 시 detectAnomalies 실행
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectionService {

    private final AnomalyLogRepository anomalyLogRepository;
    private final ProductEventLogRepository productEventLogRepository;
    private final WebSocketService webSocketService;
    private final EventHistoryCache eventHistoryCache;

    /**
     * 개선사항(1): EPC 국내산 판별 규칙
     * - 예: "001." 으로 시작하면 국내산, 나머지는 수입산
     * - 실제 EPC 체계가 "001.88..." 만 국내산이라면 조건을 더 정밀하게 (예: startsWith("001.880"))
     */
    private static final String DOMESTIC_PREFIX = "001.";

    /**
     * 📌 이벤트 처리
     * 1) DB 저장(이미 anomaly로 찍힌 EPC는 중복표시)
     * 2) 캐시에 저장
     * 3) 필수 이벤트 충족 시 -> detectAnomalies
     */
    public void processEvent(ProductEventLog eventLog) {
        if (eventLog == null || eventLog.getProduct() == null) {
            log.warn("❗ [processEvent] 이벤트 데이터가 NULL");
            return;
        }
        String epcCode = eventLog.getProduct().getEpcCode();

        // 이미 anomaly로 찍힌 EPC는 중복 표시
        boolean alreadyAnomaly = anomalyLogRepository.existsByEpcCode(epcCode);
        eventLog.setIsAnomaly(alreadyAnomaly);
        productEventLogRepository.save(eventLog);

        // 캐시에 추가
        eventHistoryCache.addEvent(eventLog);
        List<ProductEventLog> storedEvents = eventHistoryCache.getEventsByEpc(epcCode);

        // 국내/수입산 판단
        boolean isDomestic = isDomesticProduct(epcCode);

        // 필수 이벤트 목록 로드
        List<String> requiredEvents = isDomestic ? getDomesticEventFlow() : getImportedEventFlow();

        // 필수 이벤트가 모두 모였는지 확인
        boolean allEventsPresent = requiredEvents.stream().allMatch(reqEv ->
            storedEvents.stream().anyMatch(e -> e.getEvent().getEventType().equals(reqEv))
        );

        if (!allEventsPresent) {
            // 개선사항(2): "일부 필수 이벤트가 없어도 순서 검증만 하겠다" 등 확장 가능
            log.warn("⚠️ [이상 탐지 미실행] EPC={} | 아직 필수 이벤트를 모두 수집하지 않음", epcCode);
            return;
        }

        // 필수 이벤트가 모두 모였다면 이상 탐지
        log.info("🚀 [이상 탐지 실행] EPC={} | 모든 필수 이벤트 수집 완료", epcCode);
        detectAnomalies(epcCode, storedEvents, requiredEvents);
    }

    /**
     * 📌 이상 탐지 실행
     * - 이벤트 순서, 데이터 변조, 국내/수입 첫 이벤트 검증, 허브 이동 없이 판매, 허용되지 않은 이벤트 등
     */
    private void detectAnomalies(String epcCode, List<ProductEventLog> events, List<String> requiredEvents) {
        log.info("🚀 [detectAnomalies] EPC={} | 이벤트 수={}", epcCode, events.size());

        // (1) 이벤트 순서 오류
        if (!isEventSequenceValid(events, requiredEvents)) {
            saveAnomalyLog(events.get(0), "이벤트 순서 오류", "정상 흐름 불일치");
            return;
        }

        // (2) 데이터 변조 감지
        // 개선사항(3): hubName, eventType은 정상 이동일 수 있으므로 제외
        //            EPC와 productSerial, productName 등만 비교
        if (isDataTampered(events)) {
            saveAnomalyLog(events.get(0), "데이터 변조 감지", "EPC / 상품 정보 변조 의심");
            return;
        }

        // (3) 국내산 첫 이벤트 검증
        if (isDomesticProduct(epcCode) && !isDomesticFirstEventValid(events)) {
            // 예: commissioning 이후 첫 이벤트가 aggregation/WMS_inbound 아니면 "위조"
            saveAnomalyLog(events.get(0), "위조", "commissioning 이후 첫 이벤트가 올바르지 않음");
            return;
        }

        // (4) 수입산 첫 이벤트 검증
        if (!isDomesticProduct(epcCode) && !isImportedFirstEventValid(events)) {
            // 예: custom_inbound 이후 첫 이벤트가 custom_outbound 아니면 "밀수"
            saveAnomalyLog(events.get(0), "밀수", "custom_inbound 이후 첫 이벤트가 잘못됨");
            return;
        }

        // (5) 허브 이동 없이 판매
        if (isDirectlySoldWithoutHub(epcCode)) {
            // 개선사항(4): 허브 거치지 않아도 판매가 가능하면 이 로직 해제
            saveAnomalyLog(events.get(0), "불법 유통", "허브 이동 없이 판매 발생");
            return;
        }

        // (6) 허용되지 않은 이벤트(불법 이벤트)
        if (hasUnauthorizedEvent(epcCode)) {
            saveAnomalyLog(events.get(0), "이상 이벤트 발생", "허용되지 않은 이벤트 탐지됨");
            return;
        }

        // (7) AI 연동 예시 (주석)
        // boolean isAnomalous = fastAPIService.isAnomalous(events);
        boolean isAnomalous = false; // 실제 AI 호출 예시
        if (isAnomalous) {
            saveAnomalyLog(events.get(0), "AI 기반 이상 탐지", "AI 모델이 이상치로 분류");
            return;
        }

        // 여기까지 무사 통과하면 정상 처리
        log.info("✅ [정상 이벤트] EPC={} | 모든 규칙 이상 없음", epcCode);
        for (ProductEventLog e : events) {
            e.setIsAnomaly(false);
            productEventLogRepository.save(e);
        }

        // 개선사항(5): 필수 이벤트까지 모두 끝난 EPC라면 캐시에서 제거(메모리 절약)
        eventHistoryCache.removeEventHistory(epcCode);
    }

    /**
     * 📌 이벤트 순서 검증
     * - 실제 이벤트 발생 시간을 정렬 → requiredEvents와 일치하는지 확인
     * - 개선사항: 예외 상황(반품, 재포장) 많으면 상태 기계(FSM)나 룰 엔진을 활용
     */
    private boolean isEventSequenceValid(List<ProductEventLog> events, List<String> requiredEvents) {
        // 시간 순 정렬
        List<String> actualSequence = events.stream()
                .sorted(Comparator.comparing(ProductEventLog::getEventTime))
                .map(e -> e.getEvent().getEventType())
                .collect(Collectors.toList());

        int idx = 0;
        for (String ev : actualSequence) {
            if (idx < requiredEvents.size() && ev.equals(requiredEvents.get(idx))) {
                idx++;
            }
        }
        return (idx == requiredEvents.size());
    }

    /**
     * 📌 데이터 변조 감지
     * - 이전 이벤트 대비 EPC나 ProductSerial, ProductName이 바뀌었는지 점검
     * - 개선사항: 상품명이 변경될 수도 있다면 예외 처리가 필요
     */
    private boolean isDataTampered(List<ProductEventLog> events) {
        if (events.size() < 2) return false;

        ProductEventLog latest = events.get(events.size() - 1);
        ProductEventLog prev = events.get(events.size() - 2);

        boolean epcChanged = !Objects.equals(latest.getProduct().getEpcCode(), prev.getProduct().getEpcCode());
        boolean serialChanged = !Objects.equals(latest.getProduct().getProductSerial(), prev.getProduct().getProductSerial());
        boolean nameChanged = !Objects.equals(latest.getProduct().getProductName(), prev.getProduct().getProductName());

        // 여기서는 epc 또는 serial, productName 중 하나만 달라도 변조로 본다
        // 필요 시 productName 비교를 제외하거나, 별도 예외 허용 가능
        boolean tampered = (epcChanged || serialChanged || nameChanged);
        if (tampered) {
            log.warn("🚨 [데이터 변조 감지] EPC={}", latest.getProduct().getEpcCode());
        }
        return tampered;
    }

    /**
     * 📌 국내산 첫 이벤트 검증
     * - commissioning 이후 'aggregation' or 'WMS_inbound'가 와야 함 (예시)
     * - 개선사항: 추가 허용 이벤트가 있으면 이 로직 수정
     */
    private boolean isDomesticFirstEventValid(List<ProductEventLog> events) {
        if (events.size() < 2) return false;
        String secondEventType = events.get(1).getEvent().getEventType();
        return List.of("aggregation", "WMS_inbound").contains(secondEventType);
    }

    /**
     * 📌 수입산 첫 이벤트 검증
     * - custom_inbound 이후 'custom_outbound'가 와야 함 (예시)
     * - 개선사항: 허브 입고를 허용하려면 수정
     */
    private boolean isImportedFirstEventValid(List<ProductEventLog> events) {
        if (events.size() < 2) return false;
        String secondEventType = events.get(1).getEvent().getEventType();
        return "custom_outbound".equals(secondEventType);
    }

    /**
     * 📌 허브 이동 없이 판매
     * - stock_inbound(HUB) 이벤트가 한 번도 없이 stock_outbound(Sell)이 있으면 "불법"
     * - 개선사항: 실제 운영에서 직판매가 가능하다면 로직 해제
     */
    private boolean isDirectlySoldWithoutHub(String epcCode) {
        long hubInboundCount = productEventLogRepository.countByProductEpcCodeAndEventEventType(epcCode, "stock_inbound(HUB)");
        boolean sold = productEventLogRepository.existsByProductEpcCodeAndEventEventType(epcCode, "stock_outbound(Sell)");
        return (hubInboundCount == 0 && sold);
    }

    /**
     * 📌 허용되지 않은 이벤트 목록
     * - illegal_transfer, fake_aggregation, unauthorized_custom 등
     * - 개선사항: 실제 DB나 설정 파일에서 불법 이벤트를 관리 가능
     */
    private boolean hasUnauthorizedEvent(String epcCode) {
        List<String> unauthorized = List.of("illegal_transfer", "fake_aggregation", "unauthorized_custom");
        return productEventLogRepository.findByProductEpcCode(epcCode).stream()
                .map(e -> e.getEvent().getEventType())
                .anyMatch(unauthorized::contains);
    }

    /**
     * 📌 이상 탐지 발생 시 anomaly_log에 기록 + WebSocket 알림
     */
    private void saveAnomalyLog(ProductEventLog eventLog, String anomalyType, String reason) {
        if (eventLog == null || eventLog.getProduct() == null || 
            eventLog.getEvent() == null || eventLog.getHub() == null) {
            log.warn("❗ [이상 탐지 저장 실패] 필수 정보가 NULL");
            return;
        }

        String epcCode = eventLog.getProduct().getEpcCode();
        log.warn("🚨 [이상 탐지 발생] EPC={} | 유형={} | 이유={}", epcCode, anomalyType, reason);

        // 이벤트 로그 anomaly 표시
        eventLog.setIsAnomaly(true);
        productEventLogRepository.save(eventLog);

        // anomaly_log 작성
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

        // WebSocket 알림
        AnomalyDTO alert = AnomalyDTO.builder()
                .anomalyType(anomalyLog.getAnomalyType())
                .reason(anomalyLog.getReason())
                .epcCode(anomalyLog.getEpcCode())
                .build();
        webSocketService.sendAnomalyAlert(Collections.singletonList(alert));
    }

    /**
     * 📌 EPC가 "001."으로 시작하면 국내산, 그 외는 수입산 (예시)
     * - 개선사항: 필요 시 "001.880" 체크로 변경
     */
    private boolean isDomesticProduct(String epcCode) {
        return epcCode != null && epcCode.startsWith(DOMESTIC_PREFIX);
    }

    /**
     * 📌 국내산 필수 이벤트 흐름(예시)
     * - 개선사항: 실제 현장에 맞게 추가/삭제
     */
    private List<String> getDomesticEventFlow() {
        return List.of("commissioning", "aggregation", "WMS_inbound", "WMS_outbound",
                       "stock_inbound(HUB)", "stock_outbound(HUB)",
                       "stock_inbound(Wholesaler)", "stock_outbound(Wholesaler)",
                       "stock_inbound(Reseller)", "stock_outbound(Sell)");
    }

    /**
     * 📌 수입산 필수 이벤트 흐름(예시)
     * - 개선사항: 만약 custom_outbound를 안 거치고 바로 HUB로 갈 수 있다면 수정 필요
     */
    private List<String> getImportedEventFlow() {
        return List.of("custom_inbound", "custom_outbound",
                       "stock_inbound(HUB)", "stock_outbound(HUB)",
                       "stock_inbound(Wholesaler)", "stock_outbound(Wholesaler)",
                       "stock_inbound(Reseller)", "stock_outbound(Sell)");
    }
}
