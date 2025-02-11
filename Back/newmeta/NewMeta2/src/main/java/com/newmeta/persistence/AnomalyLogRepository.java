package com.newmeta.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newmeta.domain.AnomalyLog;
import com.newmeta.domain.dto.AnomalyDTO;

/**
 * 📌 이상 탐지 데이터 JPA Repository
 */
public interface AnomalyLogRepository extends JpaRepository<AnomalyLog, Long> {

    /**
     * 🚀 이상 탐지 데이터 페이징 조회
     * - ProductEventLog와 연관된 데이터까지 페이징 처리하며 AnomalyDTO로 반환합니다.
     */
    @Query("""
        SELECT new com.newmeta.domain.dto.AnomalyDTO(
            a.anomalyId, a.anomalyType, a.reason, 
            p.epcCode, p.productName, e.eventType, 
            h.hubType, h.latitude, h.longitude, a.productEventLog.eventTime)
        FROM AnomalyLog a
        JOIN a.productEventLog pel
        JOIN pel.product p
        JOIN pel.event e
        JOIN pel.hub h
    """)
    Page<AnomalyDTO> findAllAnomalyDetails(Pageable pageable);

    /**
     * 🚀 특정 기간 내 이상 탐지 조회 (ProductEventLog의 eventTime 기준)
     */
    @Query("""
        SELECT a FROM AnomalyLog a 
        WHERE a.productEventLog.eventTime BETWEEN :startDate AND :endDate
    """)
    List<AnomalyLog> findByEventTimeBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 🚀 특정 EPC 코드로 이상 탐지 조회
     */
    @Query("""
        SELECT a FROM AnomalyLog a 
        WHERE a.productEventLog.product.epcCode = :epcCode
    """)
    List<AnomalyLog> findByEpcCode(@Param("epcCode") String epcCode);

    /**
     * 🚀 특정 제품명으로 이상 탐지 조회
     */
    @Query("""
        SELECT a FROM AnomalyLog a 
        WHERE a.productEventLog.product.productName = :productName
    """)
    List<AnomalyLog> findByProductName(@Param("productName") String productName);

    /**
     * 🚀 특정 EPC 코드와 이벤트 시간으로 중복 여부 확인
     */
    @Query("""
        SELECT COUNT(a) > 0 FROM AnomalyLog a 
        WHERE a.productEventLog.product.epcCode = :epcCode 
          AND a.productEventLog.eventTime = :eventTime
    """)
    boolean existsByEpcCodeAndEventTime(@Param("epcCode") String epcCode, @Param("eventTime") Date eventTime);
}
