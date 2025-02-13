package com.newmeta.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.persistence.ProductEventLogRepository;
import com.newmeta.service.SCMDataService;
import com.newmeta.service.WebSocketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/scm")
@RequiredArgsConstructor
public class SCMController {

    private final SCMDataService scmDataService;
    private final WebSocketService webSocketService;
    private final ProductEventLogRepository productEventLogRepository;

    /**
     * 🚀 [SCM 데이터 조회] - 필터링 추가
     */
    @GetMapping("/data")
    public ResponseEntity<List<Map<String, Object>>> getSCMData(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String hubType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("📡 SCM 데이터 조회 요청: eventType={}, hubType={}, startDate={}, endDate={}", eventType, hubType, startDate, endDate);
        return ResponseEntity.ok(scmDataService.getFilteredSCMData(eventType, hubType, startDate, endDate));
    }

    /**
     * 🚀 [이상 탐지 데이터 조회 API]
     */
    @GetMapping("/anomalies")
    public ResponseEntity<List<ProductEventLogDTO>> getAnomalies() {
        log.info("📡 이상 탐지 데이터 조회 요청");
        List<ProductEventLogDTO> anomalies = scmDataService.getAnomalyData();
        if (anomalies.isEmpty()) {
            log.warn("⚠️ 이상 탐지 데이터 없음");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(anomalies);
    }

    /**
     * 🚀 [HUB별 물류량 조회 API]
     */
    @GetMapping("/hub-statistics")
    public ResponseEntity<Map<String, Long>> getHubStatistics() {
        log.info("📡 허브별 물류량 조회 요청");
        return ResponseEntity.ok(scmDataService.getHubStatistics());
    }


    /**
     * ✅ [ProductEventLog → DTO 변환 메서드]
     */
    private ProductEventLogDTO convertProductEventLogToDTO(ProductEventLog log) {
        return ProductEventLogDTO.builder()
                .epcCode(log.getProduct().getEpcCode())
                .productName(log.getProduct().getProductName())
                .eventType(log.getEvent().getEventType())
                .hubType(log.getHub().getHubType())
                .eventTime(log.getEventTime())
                .latitude(log.getHub().getLatitude())
                .longitude(log.getHub().getLongitude())
                .build();
    }


    /**
     * 🚀 [WebSocket을 통해 실시간 이상 탐지 데이터 전송]
     */
    @PostMapping("/anomalies/websocket")
    public ResponseEntity<Void> sendAnomalyDataToWebSocket() {
        log.info("📡 이상 탐지 데이터 WebSocket 전송 요청");
        scmDataService.sendAnomalyDataToWebSocket();
        return ResponseEntity.ok().build();
    }
    
    /**
     * 🚀 [허브별 실시간 물류 데이터 조회 API]
     * - SCMDataService의 getSCMData() 메서드를 호출하여 허브별 집계 데이터를 JSON 형태로 반환합니다.
     */
    @GetMapping("/hub-wise-data")
    public ResponseEntity<Map<String, Object>> getHubWiseSCMData() {
        log.info("📡 허브별 실시간 물류 데이터 조회 요청");
        Map<String, Object> hubWiseData = scmDataService.getSCMData();
        System.out.println("[SCMController] Hub-wise SCM Data: " + hubWiseData); // 콘솔 출력
        return ResponseEntity.ok(hubWiseData);
    }
}
