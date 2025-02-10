package com.newmeta.persistence;

import com.newmeta.domain.AnomalyLog;
import com.newmeta.domain.dto.AnomalyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 📌 이상 탐지 데이터 JPA Repository
 */
public interface AnomalyLogRepository extends JpaRepository<AnomalyLog, Long> {

    /**
     * 🚀 이상 탐지 데이터 페이징 조회
     */
//	@Query("SELECT new com.newmeta.domain.dto.AnomalyDTO(" +
//	           "a.anomalyId, a.anomalyType, a.reason, " +
//	           "p.epcCode, p.productName, e.eventType, " +
//	           "h.hubName, h.latitude, h.longitude, a.anomalyTimestamp) " +
//	           "FROM AnomalyLog a " +
//	           "JOIN a.productEventLog pel " +
//	           "JOIN pel.product p " +
//	           "JOIN pel.event e " +
//	           "JOIN pel.hub h")
//	    Page<AnomalyDTO> findAllAnomalyDetails(Pageable pageable);

    /**
     * 🚀 특정 EPC 코드로 이상 탐지 조회
     */
    List<AnomalyLog> findByEpcCode(String epcCode);

    /**
     * 🚀 특정 제품명으로 이상 탐지 조회
     */
    List<AnomalyLog> findByAnomalyProductName(String productName);


    /**
     * 🚀 특정 날짜 범위의 이상 탐지 조회
     */
    List<AnomalyLog> findByAnomalyTimestampBetween(Date startDate, Date endDate);

	boolean existsByEpcCode(String epcCode);

	boolean existsByEpcCodeAndAnomalyTimestamp(String epcCode, Date eventTime);

}
