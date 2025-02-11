package com.newmeta.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.persistence.AnomalyLogRepository;
import com.newmeta.persistence.EventRepository;
import com.newmeta.persistence.HubRepository;
import com.newmeta.persistence.ProductEventLogRepository;
import com.newmeta.persistence.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 📌 제품 이벤트 로그 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventLogService {

	// ✅ JPA Repository 의존성 주입
    private final ProductEventLogRepository productEventLogRepo;
    private final ProductRepository productRepo;
    private final HubRepository hubRepo;
    private final EventRepository eventRepo;
    private final AnomalyLogRepository anomalyLogRepo;
//    private final AnomalyDetectionService anomalyDetectionService; // 이상 탐지 서비스

    // ✅ 날짜 변환을 위한 포맷 설정 (yyyy-MM-dd HH:mm:ss)
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    
    /**
     * ✅ ProductEventLog -> ProductEventLogDTO 변환 (출력 JSON 맞춤)
     */
    private ProductEventLogDTO convertToDTO(ProductEventLog log) {
        return ProductEventLogDTO.builder()
            .productEventLogId(log.getProductEventLogId())
            .eventTime(log.getEventTime())
            .epcCode(log.getProduct().getEpcCode())
            .productName(log.getProduct().getProductName())
            .productSerial(log.getProduct().getProductSerial()) // 추가 확인
            .eventType(log.getEvent().getEventType())
            .hubType(log.getHub().getHubType())
            .latitude(log.getHub().getLatitude())
            .longitude(log.getHub().getLongitude())
            .isAnomaly(log.getIsAnomaly())
            .build();
    }


    
    /**
     * 🚀 product_event_log_id 기준으로 페이징된 제품 이벤트 로그 조회
     * @param pageable 페이징 요청 객체
     * @return 페이징된 제품 이벤트 로그 목록 (DTO 형태)
     */
    @Transactional(readOnly = true)
    public Page<ProductEventLogDTO> getPagedLogs(Pageable pageable) {
        log.info("📡 제품 이벤트 로그 페이징 조회 요청: 페이지={}, 크기={}", pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<ProductEventLogDTO> pagedLogs = productEventLogRepo.findPagedProductEventLogs(pageable);
            log.info("✅ 제품 이벤트 로그 페이징 조회 완료: 총 {} 개", pagedLogs.getTotalElements());
            return pagedLogs;
        } catch (Exception e) {
            log.error("❗ 페이징 조회 중 오류 발생: {}", e.getMessage(), e);
            return Page.empty();
        }
    }


    /**
     * 🚀 특정 EPC 코드로 이벤트 로그 조회
     */
    @Transactional(readOnly = true)
    public Optional<ProductEventLog> findLogById(Long id) {
        return productEventLogRepo.findById(id);
    }



    /**
     * 🚀 제품 이벤트 로그 저장
     */
    @Transactional
    public ProductEventLog saveLog(ProductEventLog eventLog) {
        return productEventLogRepo.save(eventLog);
    }

    /**
     * 🚀 모든 제품 이벤트 로그 조회
     */
    @Transactional(readOnly = true)
    public List<ProductEventLog> findAllLogs() {
        return productEventLogRepo.findAll();
    }

    /**
     * 🚀 특정 EPC 코드로 제품 이벤트 로그 조회
     */
    @Transactional(readOnly = true)
    public List<ProductEventLog> findLogsByEpcCode(String epcCode) {
        return productEventLogRepo.findByProductEpcCode(epcCode);
    }
    

    /**
     * 🚀 특정 기간 동안의 제품 이벤트 로그 조회
     */
    @Transactional(readOnly = true)
    public List<ProductEventLog> findLogsByTimeRange(Date startDate, Date endDate) {
        return productEventLogRepo.findByEventTimeBetween(startDate, endDate);
    }

    /**
     * 🚀 특정 EPC 코드의 제품 이동 경로 조회 (DTO 변환)
     */
    public List<ProductEventLogDTO> getProductMovement(String epcCode) {
        log.info("🔍 제품 이동 경로 조회 요청: EPC 코드 = {}", epcCode);

        List<ProductEventLog> eventLogs = productEventLogRepo.findByProductEpcCodeOrderByEventTime(epcCode);
        if (eventLogs.isEmpty()) {
            log.warn("⚠️ 제품 이동 경로 없음: EPC 코드 = {}", epcCode);
            return Collections.emptyList();
        }

        return eventLogs.stream()
            .map(this::convertToDTO)
            .toList();  // Java 17+ 사용 시 toList() 메서드 사용 가능
    }

    /**
     * ✅ 날짜 문자열을 Date 타입으로 변환
     */
    private Date parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                return null;
            }

            if (dateStr.length() == 16) {
                dateStr = dateStr + ":00";
            }

            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.error("❗ Invalid date format: {}", dateStr);
            return null;
        }
    }

    /**
     * ✅ LocalDateTime을 Date로 변환
     */
    private Date convertToDate(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * ✅ 추가: Date 타입을 그대로 반환 (오류 방지)
     */
    private Date convertToDate(Date date) {
        return date; // 이미 Date 타입이므로 변환 없이 반환
    }

    
}
