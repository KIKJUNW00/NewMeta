// ✅ 해당 클래스가 `com.newmeta.service` 패키지에 포함됨
package com.newmeta.service;

import java.text.ParseException; // 날짜 변환 예외 처리
import java.text.SimpleDateFormat; // 날짜 포맷 변환을 위한 클래스
import java.time.LocalDateTime; // Java 8+ 날짜 API 사용
import java.time.ZoneId; // 타임존 변환을 위한 클래스
import java.util.Date; // 날짜 데이터 처리
import java.util.List; // 리스트 데이터 처리
import java.util.Optional; // 안전한 null 처리

import org.springframework.data.domain.Page; // 페이징 처리 객체
import org.springframework.data.domain.Pageable; // 페이징 요청 객체
import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 어노테이션

import com.newmeta.domain.Dummy; // 임시 데이터 클래스
import com.newmeta.domain.Event; // 이벤트 엔티티
import com.newmeta.domain.Hub; // 허브 엔티티
import com.newmeta.domain.Product; // 제품 엔티티
import com.newmeta.domain.ProductEventLog; // 제품 이벤트 로그 엔티티
import com.newmeta.domain.dto.ProductEventLogDTO; // DTO 클래스
// ✅ 관련 리포지토리 가져오기
import com.newmeta.persistence.AnomalyLogRepository;
import com.newmeta.persistence.EventRepository;
import com.newmeta.persistence.HubRepository;
import com.newmeta.persistence.ProductEventLogRepository;
import com.newmeta.persistence.ProductRepository;

// ✅ Lombok을 활용한 자동 코드 생성
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// ✅ Spring의 서비스 레이어로 정의
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성
@Slf4j // 로깅 기능 추가
public class ProductEventLogService {

    // ✅ JPA Repository 의존성 주입
    private final ProductEventLogRepository productEventLogRepo;
    private final ProductRepository productRepo;
    private final HubRepository hubRepo;
    private final EventRepository eventRepo;
    private final AnomalyLogRepository anomalyLogRepo;
    private final AnomalyDetectionService anomalyDetectionService; // 이상 탐지 서비스

    // ✅ 날짜 변환을 위한 포맷 설정 (yyyy-MM-dd HH:mm:ss)
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 🚀 ProductEventLog 저장 (Hub 및 Event 중복 방지)
     */
    @Transactional
    public void saveProductEventLog(Dummy dummy) {
        try {
            // ✅ Product 조회 또는 저장
            Product product = productRepo.findById(dummy.getEpcCode())
                    .orElseGet(() -> productRepo.save(Product.builder()
                            .epcCode(dummy.getEpcCode())
                            .productName(dummy.getProductName())
                            .build()));

            // ✅ Hub 조회 또는 저장 (중복 방지)
            Hub hub = hubRepo.findByHubNameAndLatitudeAndLongitude(
                    dummy.getHubType(), dummy.getLatitude(), dummy.getLongitude())
                    .orElseGet(() -> hubRepo.save(Hub.builder()
                            .hubName(dummy.getHubType())
                            .latitude(dummy.getLatitude())
                            .longitude(dummy.getLongitude())
                            .build()));

            // ✅ Event 조회 또는 저장 (중복 방지)
            Event event = eventRepo.findByEventType(dummy.getEventType())
                    .orElseGet(() -> eventRepo.save(Event.builder()
                            .eventType(dummy.getEventType())
                            .build()));

            // ✅ eventTime을 Date 타입으로 변환
            Date parsedEventTime = convertToDate(dummy.getEventTime());

            // ✅ ProductEventLog 생성 및 저장
            ProductEventLog eventLog = ProductEventLog.builder()
                    .product(product)
                    .hub(hub)
                    .event(event)
                    .eventTime(parsedEventTime)
                    .build();

            productEventLogRepo.save(eventLog);

            // ✅ 이상 탐지 서비스 호출 (이상 발생 시 ProductEventLog와 함께 저장됨)
            anomalyDetectionService.detectAnomalies(dummy, eventLog);

        } catch (Exception e) {
            log.error("❗ Error saving ProductEventLog: {}", e.getMessage());
        }
    }

    /**
     * 🚀 특정 EPC 코드로 이벤트 로그 조회
     */
    @Transactional(readOnly = true)
    public Optional<ProductEventLog> findLogById(Long id) {
        return productEventLogRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductEventLog> findAllLogs() {
        return productEventLogRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductEventLog> findLogsByEpcCode(String epcCode) {
        return productEventLogRepo.findByProductEpcCode(epcCode);
    }

    @Transactional
    public void deleteLog(Long id) {
        productEventLogRepo.deleteById(id);
    }

    @Transactional
    public ProductEventLog saveLog(ProductEventLog eventLog) {
        return productEventLogRepo.save(eventLog);
    }

    @Transactional(readOnly = true)
    public Page<ProductEventLogDTO> getPagedLogs(Pageable pageable) {
        return productEventLogRepo.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * ✅ 엔티티를 DTO로 변환
     */
    private ProductEventLogDTO convertToDTO(ProductEventLog log) {
        return new ProductEventLogDTO(
                log.getProductEventLogId(),
                log.getEventTime(),
                log.getProduct().getEpcCode(),
                log.getProduct().getProductName(),
                log.getHub().getHubName(),
                log.getEvent().getEventType(),
                log.getHub().getLatitude(),
                log.getHub().getLongitude(),
                log.getIsAnomaly()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductEventLog> findLogsByTimeRange(Date startDate, Date endDate) {
        return productEventLogRepo.findByEventTimeBetween(startDate, endDate);
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
