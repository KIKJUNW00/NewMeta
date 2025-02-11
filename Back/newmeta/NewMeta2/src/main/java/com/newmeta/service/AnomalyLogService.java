package com.newmeta.service; // 📌 서비스 클래스가 속한 패키지를 선언

import java.util.Date; // ✅ 날짜 범위 조회를 위한 Date 클래스 임포트
import java.util.List; // ✅ 리스트 데이터를 다루기 위한 List 임포트
import org.springframework.data.domain.Page; // ✅ 페이징 처리를 위한 Page 객체 임포트
import org.springframework.data.domain.Pageable; // ✅ 페이징 요청 정보를 담는 Pageable 객체 임포트
import org.springframework.stereotype.Service; // ✅ 스프링의 서비스 컴포넌트로 등록하기 위한 어노테이션 임포트

import com.newmeta.domain.AnomalyLog; // ✅ 이상 탐지 로그 엔티티 클래스 임포트
import com.newmeta.persistence.AnomalyLogRepository; // ✅ 이상 탐지 로그 저장소 인터페이스 임포트

import lombok.RequiredArgsConstructor; // ✅ final 필드에 대한 생성자를 자동 생성하는 Lombok 어노테이션
import lombok.extern.slf4j.Slf4j; // ✅ 로깅을 위한 Lombok 어노테이션

/**
 * 📌 **이상 탐지 서비스**
 * ✅ **이상 탐지 로그 데이터를 관리하는 비즈니스 로직을 처리하는 서비스**
 */
@Slf4j // ✅ 로깅 기능을 자동으로 추가하는 Lombok 어노테이션
@Service // ✅ 스프링의 서비스 계층을 나타내는 어노테이션 (Bean으로 등록됨)
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자를 자동 생성하는 Lombok 어노테이션
public class AnomalyLogService {

    // ✅ JPA Repository 의존성 주입 (이상 탐지 데이터 관리)
    private final AnomalyLogRepository anomalyLogRepo;
    /**
     * 🚀 **모든 이상 탐지 데이터 조회 (페이징 포함)**
     * ✅ 페이징된 이상 탐지 로그 목록을 반환
     * @param pageable 페이징 정보를 포함하는 객체 (page, size, sort 등)
     * @return 페이징된 이상 탐지 로그 목록
     */
    public Page<AnomalyLog> getAllAnomalyDetails(Pageable pageable) {
        return anomalyLogRepo.findAll(pageable);
    }

    // 🚀 특정 EPC 코드로 이상 탐지 조회
    public List<AnomalyLog> getAnomaliesByEpc(String epcCode) {
        return anomalyLogRepo.findByEpcCode(epcCode);
    }

    // 🚀 특정 제품명으로 이상 탐지 조회
    public List<AnomalyLog> getAnomaliesByProductName(String productName) {
        return anomalyLogRepo.findByProductName(productName);
    }

    // 🚀 특정 날짜 범위의 이상 탐지 조회 (ProductEventLog의 eventTime 기준)
    public List<AnomalyLog> getAnomaliesByDateRange(Date startDate, Date endDate) {
        return anomalyLogRepo.findByEventTimeBetween(startDate, endDate);
    }

    // 🚀 특정 EPC 코드와 이벤트 시간으로 이상 탐지 여부 확인
    public boolean isAnomalyAlreadyLogged(String epcCode, Date eventTime) {
        return anomalyLogRepo.existsByEpcCodeAndEventTime(epcCode, eventTime);
    }

    // 🚀 이상 탐지 로그 삭제
    public void deleteAnomaly(Long anomalyId) {
        anomalyLogRepo.deleteById(anomalyId);
        log.info("🗑️ 이상 탐지 로그 삭제 완료: anomalyId={}", anomalyId);
    }
}
