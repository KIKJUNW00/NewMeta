//package com.newmeta.service; // 해당 패키지의 이름을 정의
//
//import java.util.Collections; // Collections 유틸리티 클래스를 가져옴
//import java.util.List; // List 인터페이스를 가져옴
//import java.util.Map; // Map 인터페이스를 가져옴
//import java.util.Optional; // Optional 클래스를 가져옴
//
//import org.springframework.stereotype.Service;  // Spring의 서비스 계층을 정의하는 어노테이션
//
//import com.newmeta.cache.EventHistoryCache; // 이벤트 히스토리를 캐시에 저장 및 조회하는 클래스
//import com.newmeta.domain.AnomalyLog;          // 이상 탐지 기록을 저장하는 엔티티
//import com.newmeta.domain.ProductEventLog;     // 제품 이벤트 로그를 저장하는 엔티티
//import com.newmeta.domain.dto.AnomalyDTO;       // 이상 탐지 관련 데이터를 전송하는 DTO
//import com.newmeta.domain.dto.PredictionResultDTO;
//import com.newmeta.persistence.AnomalyLogRepository;      // 이상 탐지 로그를 데이터베이스에 저장하는 리포지토리
//import com.newmeta.persistence.ProductEventLogRepository; // 제품 이벤트 로그를 데이터베이스에 저장하는 리포지토리
//
//import lombok.RequiredArgsConstructor; // Lombok을 사용하여 생성자 기반 의존성 주입을 쉽게 함
//import lombok.extern.slf4j.Slf4j;      // Lombok을 사용하여 로그를 쉽게 작성할 수 있도록 함
//
///**
// * 📌 이상 탐지 로직 서비스
// *  - 이벤트가 들어올 때마다 processEvent 호출
// *  - 캐시에 이벤트 저장 후, 국내/수입산 여부에 따라 "필수 이벤트"가 충분히 모이면 detectAnomalies 수행
// */
//@Service // 이 클래스가 서비스 계층임을 나타냄
//@RequiredArgsConstructor // final 필드를 인자로 받는 생성자를 자동으로 생성
//@Slf4j // SLF4J 로깅을 위한 어노테이션
//public class AnomalyDetectionService {
//
//    private final AnomalyLogRepository anomalyLogRepository; // 이상 탐지 로그를 저장하는 리포지토리
//    private final ProductEventLogRepository productEventLogRepository; // 제품 이벤트 로그를 저장하는 리포지토리
//    private final WebSocketService webSocketService; // 웹소켓을 통해 알림을 전송하는 서비스
//    private final EventHistoryCache eventHistoryCache; // 이벤트 히스토리를 저장하는 캐시
//    private final FastAPIService fastAPIService; // AI 기반의 이상 탐지를 수행하는 서비스
//
//    private static final String DOMESTIC_PREFIX = "001.880"; // 국내 제품의 EPC 코드 접두사
//    private static final int DOMESTIC_EVENT_LIMIT = 10; // 국내 제품의 이벤트 수 제한
//    private static final int IMPORTED_EVENT_LIMIT = 8; // 수입 제품의 이벤트 수 제한
//
//    // 이벤트 로그를 처리하는 메서드
//    public void processEvent(ProductEventLog eventLog) {
//        if (eventLog == null || eventLog.getProduct() == null) { // 이벤트 로그가 null인지 확인
//            log.warn("❗ [processEvent] 이벤트 데이터가 NULL"); // 경고 로그 출력
//            return; // 메서드 종료
//        }
//        String epcCode = eventLog.getProduct().getEpcCode(); // 제품의 EPC 코드 추출
//
//        eventLog.setIsAnomaly(false); // 이벤트 로그의 이상 여부를 false로 설정
//        productEventLogRepository.save(eventLog); // 이벤트 로그를 데이터베이스에 저장
//
//        eventHistoryCache.addEvent(eventLog); // 이벤트를 캐시에 추가
//        List<ProductEventLog> storedEvents = eventHistoryCache.getEventsByEpc(epcCode); // EPC 코드에 해당하는 저장된 이벤트 조회
//
//        boolean isDomestic = isDomesticProduct(epcCode); // 제품이 국내산인지 확인
//        int eventLimit = isDomestic ? DOMESTIC_EVENT_LIMIT : IMPORTED_EVENT_LIMIT; // 국내산인지에 따라 이벤트 수 제한 설정
//        if (storedEvents.size() < eventLimit) { // 저장된 이벤트 수가 제한보다 적으면
//            log.warn("⏳ [이벤트 대기] EPC={} | 현재 이벤트 수={} / 필요 이벤트 수={}",
//                    epcCode, storedEvents.size(), eventLimit); // 경고 로그 출력
//            return; // 메서드 종료
//        }
//
//        log.info("🚀 [이상 탐지 실행 준비 완료] EPC={} | 이벤트 수={}", epcCode, storedEvents.size()); // 정보 로그 출력
//        detectAnomalies(epcCode, storedEvents); // 이상 탐지 메서드 호출
//    }
//
//    // 이상 탐지를 수행하는 메서드
//    private void detectAnomalies(String epcCode, List<ProductEventLog> events) {
//        log.info("🚀 [detectAnomalies] EPC={} | 이벤트 수={}", epcCode, events.size()); // 정보 로그 출력
//
//        // 국내산 첫 이벤트 문제 탐지
//        Optional<ProductEventLog> domesticIssue = findDomesticFirstEventIssue(epcCode, events);
//        if (domesticIssue.isPresent()) { // 문제가 발견되면
//            saveAnomalyLog(domesticIssue.get(), "위조", "commissioning 이후 첫 이벤트가 올바르지 않음"); // 이상 로그 저장
//            return; // 메서드 종료
//        }
//
//        // 수입산 첫 이벤트 문제 탐지
//        Optional<ProductEventLog> importedIssue = findImportedFirstEventIssue(epcCode, events);
//        if (importedIssue.isPresent()) { // 문제가 발견되면
//            saveAnomalyLog(importedIssue.get(), "밀수", "custom_inbound 이후 첫 이벤트가 잘못됨"); // 이상 로그 저장
//            return; // 메서드 종료
//        }
//
//        // 직접 판매 이벤트 탐지
//        Optional<ProductEventLog> directSellEvt = findDirectSellEvent(events);
//        if (directSellEvt.isPresent()) { // 문제가 발견되면
//            saveAnomalyLog(directSellEvt.get(), "불법 유통", "허브 이동 없이 판매 발생"); // 이상 로그 저장
//            return; // 메서드 종료
//        }
//
//        // 허가되지 않은 이벤트 탐지
//        Optional<ProductEventLog> unauthorizedEvt = findUnauthorizedEvent(events);
//        if (unauthorizedEvt.isPresent()) { // 문제가 발견되면
//            saveAnomalyLog(unauthorizedEvt.get(), "이상 이벤트 발생", "허용되지 않은 이벤트 탐지됨"); // 이상 로그 저장
//            return; // 메서드 종료
//        }
//
//        // 이벤트 순서 오류 탐지
//        Optional<ProductEventLog> seqErrorEvt = findSequenceErrorEvent(epcCode, events);
//        if (seqErrorEvt.isPresent()) { // 문제가 발견되면
//            saveAnomalyLog(seqErrorEvt.get(), "이벤트 순서 오류", "정상 흐름 불일치"); // 이상 로그 저장
//            return; // 메서드 종료
//        }
//
//     // AI 기반 이상 탐지
//        List<PredictionResultDTO> aiResults = fastAPIService.detectAnomalies(events); // FastAPIService를 호출하여 이상 탐지 결과를 받아옴
//        if (!aiResults.isEmpty()) { // AI 결과가 비어있지 않은 경우
//            for (PredictionResultDTO result : aiResults) { 
//                String anomalyEpc = result.getEpcCode(); // 결과에서 EPC 코드 추출
//                boolean isAnomaly = result.isAnomaly(); // IsAnomaly 값을 가져옴
//
//                Optional<ProductEventLog> aiEvt = events.stream() // 이벤트 목록에서
//                        .filter(e -> e.getProduct().getEpcCode().equals(anomalyEpc)) // AI 결과의 EPC 코드와 일치하는 이벤트를 필터링
//                        .findFirst(); // 첫 번째 매칭되는 이벤트를 Optional로 반환
//
//                if (aiEvt.isPresent()) { // 매칭되는 이벤트가 존재하는 경우
//                    if (isAnomaly) { // IsAnomaly 값이 true이면 이상 데이터로 처리
//                        saveAnomalyLog(aiEvt.get(), "AI 기반 이상 탐지", "LSTM 모델이 이상 패턴 감지");
//                    } else { // IsAnomaly 값이 false이면 정상 데이터로 처리
//                        aiEvt.get().setIsAnomaly(false);
//                        productEventLogRepository.save(aiEvt.get());
//                        log.info("✅ [정상 이벤트 저장 - AI 결과] EPC={} | LSTM 모델 결과: 정상", anomalyEpc);
//                    }
//                } else { // 매칭되는 이벤트가 없는 경우
//                    if (isAnomaly) { // IsAnomaly 값이 true이면 첫 번째 이벤트를 사용하여 이상 로그 저장 (fallback)
//                        saveAnomalyLog(events.get(0), "AI 기반 이상 탐지", "LSTM 모델 이상 패턴 (fallback)");
//                    } else { // IsAnomaly 값이 false이면 첫 번째 이벤트를 정상 데이터로 저장
//                        events.get(0).setIsAnomaly(false);
//                        productEventLogRepository.save(events.get(0));
//                        log.info("✅ [정상 이벤트 저장 - AI 결과] EPC={} | LSTM 모델 결과: 정상 (fallback)", events.get(0).getProduct().getEpcCode());
//                    }
//                }
//            }
//            return; // 메서드 종료
//        }
//
//        // 모든 이벤트를 정상으로 저장
//        for (ProductEventLog e : events) {
//            e.setIsAnomaly(false); // 이상 여부를 false로 설정
//            productEventLogRepository.save(e); // 데이터베이스에 저장
//            log.info("✅ [정상 이벤트 저장] EPC={} | 모든 규칙 이상 없음", epcCode); // 정보 로그 출력
//        }
//        
//        log.info("📝 [이벤트 히스토리 삭제 전 로그 기록] EPC={} | 이벤트 수={} | 이벤트 목록={}", epcCode, events.size(), events); // 정보 로그 출력
//        eventHistoryCache.removeEventHistory(epcCode); // 캐시에서 이벤트 히스토리 삭제
//    }
// // 국내산 첫 이벤트 문제를 찾는 메서드
//    private Optional<ProductEventLog> findDomesticFirstEventIssue(String epcCode, List<ProductEventLog> events) {
//        // 제품이 국내산이 아니거나 이벤트 수가 2 미만이면 빈 Optional 반환
//        if (!isDomesticProduct(epcCode) || events.size() < 2) return Optional.empty();
//        ProductEventLog secondEvt = events.get(1); // 두 번째 이벤트 추출
//        // 두 번째 이벤트의 타입이 aggregation 또는 WMS_inbound이 아닐 경우 해당 이벤트 반환
//        if (!List.of("aggregation", "WMS_inbound").contains(secondEvt.getEvent().getEventType())) {
//            return Optional.of(secondEvt);
//        }
//        return Optional.empty(); // 조건에 맞지 않으면 빈 Optional 반환
//    }
//
//    // 수입산 첫 이벤트 문제를 찾는 메서드
//    private Optional<ProductEventLog> findImportedFirstEventIssue(String epcCode, List<ProductEventLog> events) {
//        // 제품이 국내산이면 빈 Optional 반환, 또는 이벤트 수가 2 미만이면 빈 Optional 반환
//        if (isDomesticProduct(epcCode) || events.size() < 2) return Optional.empty();
//        ProductEventLog secondEvt = events.get(1); // 두 번째 이벤트 추출
//        // 두 번째 이벤트의 타입이 custom_outbound가 아닐 경우 해당 이벤트 반환
//        if (!"custom_outbound".equals(secondEvt.getEvent().getEventType())) {
//            return Optional.of(secondEvt);
//        }
//        return Optional.empty(); // 조건에 맞지 않으면 빈 Optional 반환
//    }
//
//    // 직접 판매 이벤트를 찾는 메서드
//    private Optional<ProductEventLog> findDirectSellEvent(List<ProductEventLog> events) {
//        // 허브에서 들어온 이벤트가 있는지 확인
//        boolean hasHubInbound = events.stream()
//                .anyMatch(e -> "stock_inbound(HUB)".equals(e.getEvent().getEventType()));
//        // 허브에서 들어온 이벤트가 없다면 판매 이벤트를 찾아서 반환
//        if (!hasHubInbound) {
//            return events.stream()
//                    .filter(e -> "stock_outbound(Sell)".equals(e.getEvent().getEventType()))
//                    .findFirst();
//        }
//        return Optional.empty(); // 허브에서 들어온 이벤트가 있으면 빈 Optional 반환
//    }
//
//    // 허가되지 않은 이벤트를 찾는 메서드
//    private Optional<ProductEventLog> findUnauthorizedEvent(List<ProductEventLog> events) {
//        // 허가되지 않은 이벤트 타입 목록
//        List<String> unauthorized = List.of("illegal_transfer", "fake_aggregation", "unauthorized_custom");
//        // 이벤트 목록에서 허가되지 않은 이벤트를 찾아서 반환
//        return events.stream()
//                .filter(e -> unauthorized.contains(e.getEvent().getEventType()))
//                .findFirst();
//    }
//
//    // 이벤트 순서 오류를 찾는 메서드
//    private Optional<ProductEventLog> findSequenceErrorEvent(String epcCode, List<ProductEventLog> events) {
//        // 제품이 국내산인지 확인
//        boolean isDomestic = isDomesticProduct(epcCode);
//        // 필요한 이벤트 흐름을 가져옴
//        List<String> required = getRequiredEventFlow(isDomestic);
//
//        int idx = 0; // 현재 인덱스 초기화
//        for (ProductEventLog evt : events) {
//            String et = evt.getEvent().getEventType(); // 현재 이벤트의 타입
//            // 현재 인덱스가 필요한 이벤트 흐름의 크기보다 작고, 이벤트 타입이 현재 인덱스의 타입과 같으면 인덱스 증가
//            if (idx < required.size() && et.equals(required.get(idx))) {
//                idx++;
//            } else if (idx < required.size()) {
//                return Optional.of(evt); // 순서 오류가 발생한 이벤트 반환
//            } else {
//                return Optional.of(evt); // 이미 필요한 이벤트 흐름이 끝났지만 추가 이벤트가 있으면 반환
//            }
//        }
//        return Optional.empty(); // 모든 이벤트가 정상 순서일 경우 빈 Optional 반환
//    }
//
//    // 이상 로그를 저장하는 메서드
//    private void saveAnomalyLog(ProductEventLog eventLog, String anomalyType, String reason) {
//        // 이벤트 로그 또는 제품 정보가 없으면 경고 로그 출력 후 종료
//        if (eventLog == null || eventLog.getProduct() == null) {
//            log.warn("❗ [이상 탐지 저장 실패] eventLog 정보 부족");
//            return;
//        }
//        String epcCode = eventLog.getProduct().getEpcCode(); // 제품의 EPC 코드 추출
//        
//        // 동일한 EPC 코드와 타임스탬프를 가진 이상 로그가 이미 존재하는지 확인
//        boolean exists = anomalyLogRepository.existsByEpcCodeAndAnomalyTimestamp(epcCode, eventLog.getEventTime());
//        if (exists) {
//            log.warn("🚨 [중복 이상 탐지] 이미 저장된 anomaly_log (EPC={}, Timestamp={})", epcCode, eventLog.getEventTime());
//            return; // 중복 로그가 있으면 종료
//        }
//        
//        log.warn("🚨 [이상 탐지 발생] EPC={} | 유형={} | 이유={}", epcCode, anomalyType, reason); // 이상 탐지 로그 출력
//
//        eventLog.setIsAnomaly(true); // 이벤트 로그의 이상 여부를 true로 설정
//        productEventLogRepository.save(eventLog); // 이벤트 로그를 데이터베이스에 저장
//
//        // 새로운 이상 로그 객체 생성
//        AnomalyLog anomalyLog = AnomalyLog.builder()
//                .anomalyType(anomalyType) // 이상 유형 설정
//                .reason(reason) // 이유 설정
//                .epcCode(epcCode) // EPC 코드 설정
//                .anomalyTimestamp(eventLog.getEventTime()) // 타임스탬프 설정
//                .anomalyEventType(eventLog.getEvent().getEventType()) // 이벤트 타입 설정
//                .anomalyHub(eventLog.getHub().getHubType()) // 허브 이름 설정
//                .anomalyProductName(eventLog.getProduct().getProductName()) // 제품 이름 설정
//                .latitude(eventLog.getHub().getLatitude()) // 위도 설정
//                .longitude(eventLog.getHub().getLongitude()) // 경도 설정
////                .productEventLog(eventLog) // 관련 이벤트 로그 설정
//                .build();
//        anomalyLogRepository.save(anomalyLog); // 이상 로그를 데이터베이스에 저장
//
//        // 웹소켓을 통해 이상 탐지 알림 전송
//        AnomalyDTO alert = AnomalyDTO.builder()
//                .anomalyId(anomalyLog.getAnomalyId()) // 이상 로그 ID 설정
//                .anomalyType(anomalyType) // 이상 유형 설정
//                .reason(anomalyLog.getReason()) // 이유 설정
//                .anomalyEventType(eventLog.getEvent().getEventType()) // 이벤트 타입 설정
//                .epcCode(anomalyLog.getEpcCode()) // EPC 코드 설정
//                .anomalyProductName(anomalyLog.getAnomalyProductName()) // 제품 이름 설정
//                .anomalyHub(anomalyLog.getAnomalyHub()) // 허브 이름 설정
//                .latitude(eventLog.getHub().getLatitude()) // 위도 설정
//                .longitude(eventLog.getHub().getLongitude()) // 경도 설정
//                .anomalyTimestamp(eventLog.getEventTime()) // 타임스탬프 설정
//                .build();
//        webSocketService.sendAnomalyAlert(Collections.singletonList(alert)); // 웹소켓을 통해 알림 전송
//    }
//
//    // 제품이 국내산인지 확인하는 메서드
//    private boolean isDomesticProduct(String epcCode) {
//        // EPC 코드가 null이 아니고, 국내 제품 접두사로 시작하는지 확인
//        return epcCode != null && epcCode.startsWith(DOMESTIC_PREFIX);
//    }
//
//    // 필요한 이벤트 흐름을 가져오는 메서드
//    private List<String> getRequiredEventFlow(boolean isDomestic) {
//        // 국내산이면 국내 이벤트 흐름을, 아니면 수입 이벤트 흐름을 반환
//        return isDomestic ? getDomesticEventFlow() : getImportedEventFlow();
//    }
//
//    // 국내 이벤트 흐름을 정의하는 메서드
//    private List<String> getDomesticEventFlow() {
//        return List.of(
//            "commissioning",
//            "aggregation",
//            "WMS_inbound",
//            "WMS_outbound",
//            "stock_inbound(HUB)",
//            "stock_outbound(HUB)",
//            "stock_inbound(Wholesaler)",
//            "stock_outbound(Wholesaler)",
//            "stock_inbound(Reseller)",
//            "stock_outbound(Sell)"
//        );
//    }
//
//    // 수입 이벤트 흐름을 정의하는 메서드
//    private List<String> getImportedEventFlow() {
//        return List.of(
//            "custom_inbound",
//            "custom_outbound",
//            "stock_inbound(HUB)",
//            "stock_outbound(HUB)",
//            "stock_inbound(Wholesaler)",
//            "stock_outbound(Wholesaler)",
//            "stock_inbound(Reseller)",
//            "stock_outbound(Sell)"
//        );
//    }
//}
