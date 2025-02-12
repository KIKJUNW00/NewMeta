//package com.newmeta.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.newmeta.domain.ProductEventLog;
//import com.newmeta.domain.dto.ProductEventLogDTO;
//import com.newmeta.service.AnomalyDetectionService;
//import com.newmeta.service.ProductEventLogService;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 하나의 컨트롤러(ReportedController) 내에 기존 원본 AnomalyDetectionService 로직을 모두 구현. 1)
// * 이벤트 생성 및 DB 저장 2) (임계치 없이) 데이터가 1개 이상이면 이상치 탐지 로직 수행 3) fastapi(AI) 호출은 "국내산
// * 10개, 수입산 8개" 이상일 때만 수행. 4) domestic/imported 첫 이벤트 검사, 직접 판매 이벤트, 허가되지 않은
// * 이벤트, 순서 오류 등 모두 포함. 5) 이상 발생 시 anomalyLog 저장, 웹소켓 알림 등까지 구현.
// */
//@RestController
//@Slf4j
//public class ReportedController2 {
//
//    @Autowired
//    private ProductEventLogService productEventLogService;
//
//    @Autowired
//    private AnomalyDetectionService anomalyDetectionService;
//
//    @PostMapping("/report")
//    public void reported(@RequestBody ProductEventLogDTO dto) {
//        log.info("[Report Controller] 이벤트 처리 시작. EPC={}, EventType={}", dto.getEpcCode(), dto.getEventType());
//
//        // 1️⃣ 이벤트 저장
//        ProductEventLog productEventLog = productEventLogService.createAndSaveEventLog(dto);
//
//        // 2️⃣ 모든 이벤트 로그 조회 및 이상 탐지
//        List<ProductEventLog> events = productEventLogService.findAllLogsByEpcCode(dto.getEpcCode());
//        if (!events.isEmpty()) {
//            anomalyDetectionService.detectAnomalies(dto.getEpcCode(), events);
//        } else {
//            log.warn("[이상 탐지 스킵] 이벤트가 없습니다.");
//        }
//
//        // 3️⃣ 최근 10개 로그 출력 (디버깅 용도)
//        productEventLogService.printRecentLogs(dto.getEpcCode());
//    }
//}
