package com.newmeta.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.ProductEventLogDTO;

@Repository
public interface ProductEventLogRepository extends JpaRepository<ProductEventLog, Long> {



    /**
     * 🚀 특정 EPC 코드로 제품 이벤트 로그 조회
     */
    List<ProductEventLog> findByProductEpcCode(String epcCode);

    /**
     * 🚀 특정 기간 동안의 제품 이벤트 로그 조회
     */
    List<ProductEventLog> findByEventTimeBetween(Date startDate, Date endDate);

    /**
     * 🚀 DTO 변환을 위한 제품 이벤트 로그 조회 (페이징 지원)
     * 🚀 product_event_log_id 기준으로 내림차순 정렬
     */
    @Query("SELECT new com.newmeta.domain.dto.ProductEventLogDTO(" +
            "pel.productEventLogId, pel.eventTime, " +
            "p.epcCode, p.productName, e.eventType, " +
            "h.hubName, h.latitude, h.longitude, pel.isAnomaly) " +
            "FROM ProductEventLog pel " +
            "JOIN pel.product p " +
            "JOIN pel.event e " +
            "JOIN pel.hub h " +
            "ORDER BY pel.productEventLogId ASC")
    Page<ProductEventLogDTO> findPagedProductEventLogs(Pageable pageable);

    // ✅ 특정 EPC 코드 및 이벤트 타입 존재 여부 확인(AnomalyDetectionService:commissioning, Custom_inbound 검증)
	boolean existsByProductEpcCodeAndEventEventType(String epcCode, String eventType);
	 Page<ProductEventLog> findAll(Pageable pageable);
	 /**
     * 특정 EPC 코드에 대한 이동 경로를 이벤트 시간 기준 정렬하여 조회
     */
    List<ProductEventLog> findByProductEpcCodeOrderByEventTime(String epcCode);

	List<ProductEventLog> findByIsAnomalyTrue();

	static List<ProductEventLog> findByEpcCode(String epcCode) {
		// TODO Auto-generated method stub
		return null;
	}
}
