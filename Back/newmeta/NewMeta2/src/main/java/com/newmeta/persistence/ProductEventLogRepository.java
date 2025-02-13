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

    // ✅ 특정 EPC 코드로 이벤트 로그 조회 (기존과 동일)
    List<ProductEventLog> findByProductEpcCode(String epcCode);

    // ✅ 특정 EPC 코드의 이벤트 로그를 이벤트 시간 기준 오름차순으로 조회
    List<ProductEventLog> findByProductEpcCodeOrderByEventTimeAsc(String epcCode);

    // ✅ 특정 EPC 코드의 이벤트 로그를 이벤트 시간 기준 내림차순으로 조회
    List<ProductEventLog> findByProductEpcCodeOrderByEventTimeDesc(String epcCode);

    // ✅ product_event_log_id 기준으로 내림차순 정렬하여 상위 10개 데이터 조회
    List<ProductEventLog> findTop10ByProductEpcCodeOrderByProductEventLogIdDesc(String epcCode);

    // ✅ 특정 EPC 코드와 이벤트 타입으로 존재 여부 확인
    boolean existsByProductEpcCodeAndEventEventType(String epcCode, String eventType);


    // ✅ 이상 탐지된 이벤트 로그 조회
    List<ProductEventLog> findByIsAnomalyTrue();

    // ✅ 특정 EPC 코드 및 이벤트 타입으로 이벤트 로그 개수 조회
    long countByProductEpcCodeAndEventEventType(String epcCode, String eventType);

    // ✅ DTO 변환을 위한 JPQL 쿼리
    @Query("SELECT new com.newmeta.domain.dto.ProductEventLogDTO(" +
           "pel.productEventLogId, pel.eventTime, p.epcCode, p.productName, " +
           "p.productSerial, e.eventType, h.hubType, h.latitude, h.longitude, pel.isAnomaly) " +
           "FROM ProductEventLog pel " +
           "JOIN pel.product p " +
           "JOIN pel.event e " +
           "JOIN pel.hub h " +
           "ORDER BY pel.productEventLogId DESC")
    Page<ProductEventLogDTO> findPagedProductEventLogs(Pageable pageable);

	List<ProductEventLog> findByProductEpcCodeOrderByEventTime(String epcCode);

	List<ProductEventLog> findAllByProduct_EpcCodeOrderByProductEventLogIdAsc(String epcCode);

	List<ProductEventLog> findTop10ByProduct_EpcCodeOrderByProductEventLogIdDesc(String epcCode);
}
