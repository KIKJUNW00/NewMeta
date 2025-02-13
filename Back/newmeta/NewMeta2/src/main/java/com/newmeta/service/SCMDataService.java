package com.newmeta.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.newmeta.domain.AnomalyLog;
import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.AnomalyDTO;
import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.persistence.AnomalyLogRepository;
import com.newmeta.persistence.ProductEventLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 📡 SCM 데이터 처리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SCMDataService {

    private final ProductEventLogRepository productEventLogRepository;
    private final AnomalyLogRepository anomalyLogRepository;
    private final WebSocketService webSocketService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 🚀 특정 필터 조건을 적용하여 SCM 데이터를 조회
     */
    public List<Map<String, Object>> getFilteredSCMData(String eventType, String hubType, String startDate, String endDate) {
        return productEventLogRepository.findAll().stream()
                .filter(log -> (eventType == null || log.getEvent().getEventType().equals(eventType)))
                .filter(log -> (hubType == null || log.getHub().getHubType().equals(hubType)))
                .filter(log -> isWithinDateRange(log.getEventTime(), startDate, endDate))
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }



    /**
     * 🚀 허브별 물류량 조회
     */
    public Map<String, Long> getHubStatistics() {
        return productEventLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(log -> log.getHub().getHubType(), Collectors.counting()));
    }

    /**
     * 🚀 날짜별 이상 탐지 발생 통계 조회
     */
    public Map<String, Long> getAnomalyDailyStatistics() {
        return anomalyLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        log -> dateFormat.format(log.getProductEventLog().getEventTime()).split(" ")[0],
                        Collectors.counting()
                ));
    }

    /**
     * 🚀 이상 탐지 원인 분석
     */
    public Map<String, Long> getAnomalyTypeStatistics() {
        return anomalyLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(AnomalyLog::getAnomalyType, Collectors.counting()));
    }
    
    /**
     * 🚀 [허브별 실시간 물류 데이터 조회]
     * - 전체 로그를 읽어 각 허브의 이름(hubName)으로 그룹핑하고, 추가 통계 데이터를 포함하여 반환합니다.
     */
    public Map<String, Object> getSCMData() {
        // 기존 코드: 허브 타입(코드)을 사용
        List<ProductEventLog> logs = productEventLogRepository.findAll();
        Map<String, Long> hubWiseData = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getHub().getHubType(), // 허브 타입으로 그룹핑
                        Collectors.counting()
                ));

        Map<String, Object> result = Map.of(
                "hubWiseData", hubWiseData,
                "domestic_total", countDomesticProducts(logs),
                "imported_total", countImportedProducts(logs),
                "anomaly_total", anomalyLogRepository.count()
        );

        System.out.println("[SCMDataService] getSCMData() 결과: " + result);
        return result;
    }

    /**
     * 🚀 WebSocket을 통해 허브별 데이터 전송
     */
    public void sendHubWiseDataToWebSocket() {
        Map<String, Object> hubWiseData = getSCMData();
        webSocketService.sendHubWiseData(hubWiseData);
        log.info("✅ WebSocket - 허브별 데이터 전송 완료");
    }

    /**
     * 🚀 WebSocket을 통해 실시간 이상 탐지 데이터 전송
     */
    public void sendAnomalyDataToWebSocket() {
        List<AnomalyDTO> anomalyData = getFilteredAnomalyData(null, null, null, null, null);
        webSocketService.sendAnomalyAlert(anomalyData);
        log.info("✅ WebSocket - 이상 탐지 데이터 전송 완료");
    }

    /**
     * 🚀 특정 필터 조건을 적용하여 이상 탐지 데이터를 조회
     */
    public List<AnomalyDTO> getFilteredAnomalyData(String epcCode, String hubType, String productName, String startDate, String endDate) {
        return anomalyLogRepository.findAll().stream()
                .filter(log -> (epcCode == null || log.getProductEventLog().getProduct().getEpcCode().equals(epcCode)))
                .filter(log -> (hubType == null || log.getProductEventLog().getHub().getHubType().equals(hubType)))
                .filter(log -> isWithinDateRange(log.getProductEventLog().getEventTime(), startDate, endDate))
                .map(this::convertAnomalyLogToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductEventLogDTO> getAnomalyData() {
        log.info("📡 이상 탐지 데이터 조회 중...");
        List<ProductEventLog> anomalyLogs = productEventLogRepository.findByIsAnomalyTrue(); // ✅ isAnomaly 필드를 기준으로 조회
        return anomalyLogs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    /**
     * ✅ ProductEventLog → Map 변환
     */
    private Map<String, Object> convertToMap(ProductEventLog log) {
        Map<String, Object> result = new HashMap<>();
        result.put("epcCode", log.getProduct().getEpcCode());
        result.put("productName", log.getProduct().getProductName());
        result.put("hub", log.getHub().getHubType());
        result.put("eventType", log.getEvent().getEventType());
        result.put("eventTime", dateFormat.format(log.getEventTime()));
        return result;
    }

    /**
     * ✅ ProductEventLog → DTO 변환
     */
    private ProductEventLogDTO convertToDTO(ProductEventLog log) {
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
     * ✅ AnomalyLog → DTO 변환
     */
    private AnomalyDTO convertAnomalyLogToDTO(AnomalyLog log) {
        return AnomalyDTO.builder()
                .anomalyId(log.getAnomalyId())
                .anomalyType(log.getAnomalyType())
                .reason(log.getReason())
                .epcCode(log.getProductEventLog().getProduct().getEpcCode())
                .productName(log.getProductEventLog().getProduct().getProductName())
                .eventType(log.getProductEventLog().getEvent().getEventType())
                .hubType(log.getProductEventLog().getHub().getHubType())
                .latitude(log.getProductEventLog().getHub().getLatitude())
                .longitude(log.getProductEventLog().getHub().getLongitude())
                .anomalyTimestamp(log.getProductEventLog().getEventTime())
                .build();
    }

    /**
     * ✅ 날짜 범위 검사
     */
    private boolean isWithinDateRange(Date eventTime, String startDate, String endDate) {
        try {
            Date start = startDate != null ? dateFormat.parse(startDate) : null;
            Date end = endDate != null ? dateFormat.parse(endDate) : null;
            return (start == null || !eventTime.before(start)) && (end == null || !eventTime.after(end));
        } catch (ParseException e) {
            log.warn("❌ 날짜 변환 오류: {}", e.getMessage());
            return false;
        }
    }

    /**
     * ✅ 국내산 제품 개수 계산
     */
    private long countDomesticProducts(List<ProductEventLog> logs) {
        return logs.stream().filter(log -> isDomesticProduct(log.getProduct().getEpcCode())).count();
    }

    /**
     * ✅ 수입산 제품 개수 계산
     */
    private long countImportedProducts(List<ProductEventLog> logs) {
        return logs.stream().filter(log -> !isDomesticProduct(log.getProduct().getEpcCode())).count();
    }

    /**
     * ✅ 국내산 여부 판별
     */
    private boolean isDomesticProduct(String epcCode) {
        return epcCode != null && epcCode.startsWith("001.880");
    }
}
