package com.newmeta.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.AnomalyLog;
import com.newmeta.domain.Event;
import com.newmeta.domain.Hub;
import com.newmeta.domain.Product;
import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.AnomalyDTO;
import com.newmeta.domain.dto.PredictionResultDTO;
import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.persistence.AnomalyLogRepository;
import com.newmeta.persistence.EventRepository;
import com.newmeta.persistence.HubRepository;
import com.newmeta.persistence.ProductEventLogRepository;
import com.newmeta.persistence.ProductRepository;
import com.newmeta.service.FastAPIService;
import com.newmeta.service.WebSocketService;

import lombok.extern.slf4j.Slf4j;

/**
 * 하나의 컨트롤러(ReportedController) 내에 기존 원본 AnomalyDetectionService 로직을 모두 구현.
 * 1) 이벤트 생성 및 DB 저장
 * 2) (임계치 없이) 데이터가 1개 이상이면 이상치 탐지 로직 수행
 * 3) fastapi(AI) 호출은 "국내산 10개, 수입산 8개" 이상일 때만 수행.
 * 4) domestic/imported 첫 이벤트 검사, 직접 판매 이벤트, 허가되지 않은 이벤트, 순서 오류 등 모두 포함.
 * 5) 이상 발생 시 anomalyLog 저장, 웹소켓 알림 등까지 구현.
 */
@RestController
@Slf4j
public class ReportedController {

    // [1] 레포지토리 및 서비스
    @Autowired
    ProductEventLogRepository productEventLogRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    HubRepository hubRepository;

    @Autowired
    AnomalyLogRepository anomalyLogRepository;

    @Autowired
    FastAPIService fastAPIService; // AI 호출

    @Autowired
    WebSocketService webSocketService; // 웹소켓 알림

    // [2] 국내산 EPC 접두사, AI 호출 시 임계치
    
    private static final String DOMESTIC_PREFIX = "001.880";
    private static final int DOMESTIC_AI_LIMIT = 10;
    private static final int IMPORTED_AI_LIMIT = 8;

    /**
     * /report : ProductEventLogDTO를 받아 로직 실행
     *  1) Product/Event/Hub 조회 or 생성
     *  2) ProductEventLog 저장
     *  3) DB에서 해당 EPC의 모든 로그 조회
     *  4) 1개 이상이면 이상치 탐지(detectAnomalies)
     *  5) 국내산은 10개 이상, 수입산은 8개 이상이면 AI 호출
     *  6) 최근 10개 로그 콘솔 출력(디버깅용)
     */
    @PostMapping("/report")
    public void reported(@RequestBody ProductEventLogDTO dto) {
        // [A] DTO -> 엔티티 변환 (Product, Event, Hub)
        Product product = productRepo.findById(dto.getEpcCode()).orElse(null);
        if (product == null) {
            product = Product.builder()
                .epcCode(dto.getEpcCode())
                .productName(dto.getProductName())
                .productSerial(dto.getProductSerial())
                .build();
        }

        Event event = eventRepository.findByEventType(dto.getEventType()).orElse(null);
        if (event == null) {
            event = Event.builder()
                .eventType(dto.getEventType())
                .build();
        }

        Hub hub = hubRepository.findById(dto.getHubType()).orElse(null);
        if (hub == null) {
            hub = Hub.builder()
                .hubType(dto.getHubType())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
        }

        // [B] ProductEventLog DB 저장
        ProductEventLog productEventLog = ProductEventLog.builder()
            .product(product)
            .eventTime(dto.getEventTime())
            .event(event)
            .hub(hub)
            .isAnomaly(false)
            .build();
        productEventLogRepo.save(productEventLog);
        log.info("[Report Controller] 새 이벤트 저장. EPC={}, EventType={}", dto.getEpcCode(), dto.getEventType());

        // [C] DB에서 동일 EPC 코드 이벤트 전체 조회 (ID 오름차순 예시)
        List<ProductEventLog> events = productEventLogRepo.findAllByProduct_EpcCodeOrderByProductEventLogIdAsc(dto.getEpcCode());
        log.info("[보고된 이벤트 수] EPC={} | count={}", dto.getEpcCode(), events.size());

        // [D] 1개 이상이면 이상치 탐지
        if (!events.isEmpty()) {
            detectAnomalies(dto.getEpcCode(), events);
        } else {
            log.warn("[이상치 검사 스킵] 이벤트가 0개");
        }

        // [E] 최근 10개 로그 콘솔 출력
        List<ProductEventLog> last10 = productEventLogRepo.findTop10ByProduct_EpcCodeOrderByProductEventLogIdDesc(dto.getEpcCode());
        log.info("[최근 10개 로그 출력] EPC={}", dto.getEpcCode());
        for (ProductEventLog logItem : last10) {
            log.info(" - {}", logItem);
        }
    }

    /**
     * 기존 AnomalyDetectionService 로직을 모두 포함:
     *  1) 국내산 첫 이벤트 검사
     *  2) 수입산 첫 이벤트 검사
     *  3) 직접 판매 이벤트
     *  4) 허가되지 않은 이벤트
     *  5) 이벤트 순서 오류
     *  6) AI 호출 (임계치: 국내10, 수입8)
     *  7) 이상 없으면 전부 정상 처리
     */
    private void detectAnomalies(String epcCode, List<ProductEventLog> events) {
        log.info("[detectAnomalies] EPC={} | 이벤트수={}", epcCode, events.size());

        // 1) 국내산 첫 이벤트 문제
        Optional<ProductEventLog> domesticIssue = findDomesticFirstEventIssue(epcCode, events);
        if (domesticIssue.isPresent()) {
            saveAnomalyLog(domesticIssue.get(), "위조", "commissioning 이후 첫 이벤트가 올바르지 않음");
            return;
        }

        // 2) 수입산 첫 이벤트 문제
        Optional<ProductEventLog> importedIssue = findImportedFirstEventIssue(epcCode, events);
        if (importedIssue.isPresent()) {
            saveAnomalyLog(importedIssue.get(), "밀수", "custom_inbound 이후 첫 이벤트가 잘못됨");
            return;
        }

        // 3) 직접 판매 이벤트 (허브 없이 판매?)
        Optional<ProductEventLog> directSellEvt = findDirectSellEvent(events);
        if (directSellEvt.isPresent()) {
            saveAnomalyLog(directSellEvt.get(), "불법 유통", "허브 이동 없이 판매 발생");
            return;
        }

        // 4) 허가되지 않은 이벤트
        Optional<ProductEventLog> unauthorizedEvt = findUnauthorizedEvent(events);
        if (unauthorizedEvt.isPresent()) {
            saveAnomalyLog(unauthorizedEvt.get(), "이상 이벤트 발생", "허용되지 않은 이벤트 탐지됨");
            return;
        }

        // 5) 이벤트 순서 오류
        Optional<ProductEventLog> seqErrorEvt = findSequenceErrorEvent(epcCode, events);
        if (seqErrorEvt.isPresent()) {
            saveAnomalyLog(seqErrorEvt.get(), "이벤트 순서 오류", "정상 흐름 불일치");
            return;
        }

        // 6) AI 호출 (국내=10, 수입=8 이상)
        boolean isDomestic = isDomesticProduct(epcCode);
        int aiLimit = isDomestic ? DOMESTIC_AI_LIMIT : IMPORTED_AI_LIMIT;
        if (events.size() >= aiLimit) {
            List<PredictionResultDTO> aiResults = fastAPIService.detectAnomalies(events);
            if (!aiResults.isEmpty()) {
                for (PredictionResultDTO result : aiResults) {
                    boolean isAnomaly = result.isAnomaly();
                    String anomalyEpc = result.getEpcCode();
                    Optional<ProductEventLog> match = events.stream()
                        .filter(e -> e.getProduct().getEpcCode().equals(anomalyEpc))
                        .findFirst();

                    if (match.isPresent()) {
                        if (isAnomaly) {
                            saveAnomalyLog(match.get(), "AI 기반 이상 탐지", "LSTM 모델 이상 패턴 감지");
                            return;
                        } else {
                            match.get().setIsAnomaly(false);
                            productEventLogRepo.save(match.get());
                            log.info("[AI 정상] EPC={}", anomalyEpc);
                        }
                    } else {
                        if (isAnomaly && !events.isEmpty()) {
                            saveAnomalyLog(events.get(0), "AI 이상(fallback)", "매칭 이벤트 없음");
                            return;
                        } else if (!events.isEmpty()) {
                            events.get(0).setIsAnomaly(false);
                            productEventLogRepo.save(events.get(0));
                        }
                    }
                }
            } else {
                log.info("[AI 결과 없음] 모든 이벤트 정상 처리");
            }
        } else {
            log.info("[AI 호출 스킵] EPC={} | 현재 수={} / 필요={}", epcCode, events.size(), aiLimit);
        }

        // 7) 이상 없으면 전부 정상 처리
        for (ProductEventLog e : events) {
            e.setIsAnomaly(false);
            productEventLogRepo.save(e);
        }
        log.info("[정상 이벤트 저장 완료] EPC={}", epcCode);
    }

    // -------------------------------------------------------------------------
    //                     기존 메서드들 (완전 구현)
    // -------------------------------------------------------------------------

    /** 국내산 여부 판단 */
    private boolean isDomesticProduct(String epcCode) {
        return epcCode != null && epcCode.startsWith(DOMESTIC_PREFIX);
    }

    /** 국내산 첫 이벤트 문제: 이벤트가 2개 이상 있어야 검사 가능 */
    private Optional<ProductEventLog> findDomesticFirstEventIssue(String epcCode, List<ProductEventLog> events) {
        if (!isDomesticProduct(epcCode) || events.size() < 2) return Optional.empty();
        ProductEventLog secondEvt = events.get(1);
        if (!List.of("aggregation", "WMS_inbound").contains(secondEvt.getEvent().getEventType())) {
            return Optional.of(secondEvt);
        }
        return Optional.empty();
    }

    /** 수입산 첫 이벤트 문제: 2개 이상 필요 */
    private Optional<ProductEventLog> findImportedFirstEventIssue(String epcCode, List<ProductEventLog> events) {
        if (isDomesticProduct(epcCode) || events.size() < 2) return Optional.empty();
        ProductEventLog secondEvt = events.get(1);
        if (!"custom_outbound".equals(secondEvt.getEvent().getEventType())) {
            return Optional.of(secondEvt);
        }
        return Optional.empty();
    }

    /** 허브 이동 없이 판매 발생(직접 판매) */
    private Optional<ProductEventLog> findDirectSellEvent(List<ProductEventLog> events) {
        boolean hasHubInbound = events.stream()
            .anyMatch(e -> "stock_inbound(HUB)".equals(e.getEvent().getEventType()));
        if (!hasHubInbound) {
            // 허브 유입 없이 곧바로 "stock_outbound(Sell)"가 발생하면 불법 유통
            return events.stream()
                .filter(e -> "stock_outbound(Sell)".equals(e.getEvent().getEventType()))
                .findFirst();
        }
        return Optional.empty();
    }

    /** 허가되지 않은 이벤트 (illegal_transfer 등) */
    private Optional<ProductEventLog> findUnauthorizedEvent(List<ProductEventLog> events) {
        List<String> unauthorized = List.of("illegal_transfer", "fake_aggregation", "unauthorized_custom");
        return events.stream()
            .filter(e -> unauthorized.contains(e.getEvent().getEventType()))
            .findFirst();
    }

    /** 이벤트 순서 오류 검사 */
    private Optional<ProductEventLog> findSequenceErrorEvent(String epcCode, List<ProductEventLog> events) {
        boolean isDomestic = isDomesticProduct(epcCode);
        List<String> flow = isDomestic ? getDomesticEventFlow() : getImportedEventFlow();

        int idx = 0;
        for (ProductEventLog evt : events) {
            String et = evt.getEvent().getEventType();
            if (idx < flow.size() && et.equals(flow.get(idx))) {
                idx++;
            } else if (idx < flow.size()) {
                return Optional.of(evt); // 순서 어긋난 이벤트
            } else {
                return Optional.of(evt); // 필요한 흐름보다 초과되는 이벤트
            }
        }
        return Optional.empty();
    }

    /** 국내산 이벤트 흐름 */
    private List<String> getDomesticEventFlow() {
        return List.of(
            "commissioning",
            "aggregation",
            "WMS_inbound",
            "WMS_outbound",
            "stock_inbound(HUB)",
            "stock_outbound(HUB)",
            "stock_inbound(Wholesaler)",
            "stock_outbound(Wholesaler)",
            "stock_inbound(Reseller)",
            "stock_outbound(Sell)"
        );
    }

    /** 수입 이벤트 흐름 */
    private List<String> getImportedEventFlow() {
        return List.of(
            "custom_inbound",
            "custom_outbound",
            "stock_inbound(HUB)",
            "stock_outbound(HUB)",
            "stock_inbound(Wholesaler)",
            "stock_outbound(Wholesaler)",
            "stock_inbound(Reseller)",
            "stock_outbound(Sell)"
        );
    }

    /**
     * 이상 로그 저장
     * - 중복 검사 (epcCode + eventTime)
     * - productEventLog.isAnomaly = true
     * - anomaly_log 테이블 저장
     * - 웹소켓 알림
     */
    private void saveAnomalyLog(ProductEventLog eventLog, String anomalyType, String reason) {
        if (eventLog == null || eventLog.getProduct() == null) {
            log.warn("[이상 탐지 저장 실패] eventLog 정보 부족");
            return;
        }
        String epcCode = eventLog.getProduct().getEpcCode();

        // 중복 검사
        boolean exists = anomalyLogRepository.existsByEpcCodeAndAnomalyTimestamp(epcCode, eventLog.getEventTime());
        if (exists) {
            log.warn("🚨 [중복 이상 탐지] 이미 저장된 anomaly_log (EPC={}, Timestamp={})", epcCode, eventLog.getEventTime());
            return;
        }

        log.warn("[이상 탐지 발생] EPC={} | 유형={} | 이유={}", epcCode, anomalyType, reason);

        // 1) 이벤트 로그 갱신
        eventLog.setIsAnomaly(true);
        productEventLogRepo.save(eventLog);

        // 2) anomaly_log 테이블에 저장
        AnomalyLog anomalyLog = AnomalyLog.builder()
            .anomalyType(anomalyType)
            .reason(reason)
            .epcCode(epcCode)
            .anomalyTimestamp(eventLog.getEventTime())
            .anomalyEventType(eventLog.getEvent().getEventType())
            .anomalyHub(eventLog.getHub().getHubType())
            .anomalyProductName(eventLog.getProduct().getProductName())
            .latitude(eventLog.getHub().getLatitude())
            .longitude(eventLog.getHub().getLongitude())
            .build();
        anomalyLogRepository.save(anomalyLog);

        // 3) 웹소켓 알림
        AnomalyDTO alert = AnomalyDTO.builder()
            .anomalyId(anomalyLog.getAnomalyId())
            .anomalyType(anomalyType)
            .reason(anomalyLog.getReason())
            .anomalyEventType(eventLog.getEvent().getEventType())
            .epcCode(anomalyLog.getEpcCode())
            .anomalyProductName(anomalyLog.getAnomalyProductName())
            .anomalyHub(anomalyLog.getAnomalyHub())
            .latitude(eventLog.getHub().getLatitude())
            .longitude(eventLog.getHub().getLongitude())
            .anomalyTimestamp(eventLog.getEventTime())
            .build();
        webSocketService.sendAnomalyAlert(Collections.singletonList(alert));
    }
}
