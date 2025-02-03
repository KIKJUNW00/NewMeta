package com.newmeta.controller; // 해당 컨트롤러 클래스가 속한 패키지를 선언

import java.text.ParseException; // 날짜 변환 예외 처리
import java.text.SimpleDateFormat; // 날짜 포맷 변환을 위한 클래스
import java.util.Date; // 날짜 데이터를 처리하기 위한 Date 클래스
import java.util.List; // 리스트 데이터를 다루기 위한 라이브러리

import org.springframework.data.domain.Page; // 페이징 처리를 위한 Page 객체 임포트
import org.springframework.data.domain.PageRequest; // 페이지 요청을 생성하기 위한 PageRequest 임포트
import org.springframework.data.domain.Pageable; // 페이징 요청 정보를 담는 Pageable 객체 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답을 처리하기 위한 ResponseEntity 임포트
import org.springframework.web.bind.annotation.*; // Spring Web 관련 어노테이션 임포트

import com.newmeta.domain.ProductEventLog; // 제품 이벤트 로그 엔티티 클래스 임포트
import com.newmeta.domain.dto.ProductEventLogDTO; // DTO 클래스 임포트
import com.newmeta.service.ProductEventLogService; // 제품 이벤트 로그 서비스 클래스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성

/**
 * 📌 제품 이벤트 로그 컨트롤러
 * ✅ 제품 이벤트 로그 조회, 저장, 삭제 기능 제공
 */
@RestController // RESTful API 컨트롤러로 등록
@RequestMapping("/producteventLog") // 모든 API 엔드포인트가 `/producteventLog`로 시작하도록 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동 생성
public class ProductEventLogController {

    private final ProductEventLogService productEventLogService; // 제품 이벤트 로그 데이터 관리 서비스

    /**
     * 🚀 페이징 처리된 데이터 반환
     * @param page 조회할 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 데이터 개수 (기본값: 30)
     * @return 페이징된 제품 이벤트 로그 목록
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<ProductEventLogDTO>> getPagedLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        Pageable pageable = PageRequest.of(page, size); // 페이지 요청 생성
        Page<ProductEventLogDTO> pagedLogs = productEventLogService.getPagedLogs(pageable); // 서비스 호출
        return ResponseEntity.ok(pagedLogs);
    }

    /**
     * 🚀 모든 제품 이벤트 로그 조회
     * @return 데이터베이스에 저장된 모든 제품 이벤트 로그 목록 반환
     */
    @GetMapping
    public ResponseEntity<List<ProductEventLog>> getAllLogs() {
        return ResponseEntity.ok(productEventLogService.findAllLogs());
    }

    /**
     * 🚀 ID로 특정 제품 이벤트 로그 조회
     * @param id 조회할 제품 이벤트 로그 ID
     * @return 해당 ID의 제품 이벤트 로그 반환 (없으면 404 Not Found 응답)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductEventLog> getLogById(@PathVariable Long id) {
        return productEventLogService.findLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 🚀 EPC 코드로 제품 이벤트 로그 조회
     * @param epcCode 조회할 제품의 EPC 코드
     * @return 해당 EPC 코드와 관련된 제품 이벤트 로그 목록 반환 (없으면 404 Not Found 응답)
     */
    @GetMapping("/epc/{epcCode}")
    public ResponseEntity<List<ProductEventLog>> getLogsByEpcCode(@PathVariable String epcCode) {
        List<ProductEventLog> logs = productEventLogService.findLogsByEpcCode(epcCode);
        if (logs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logs);
    }

    /**
     * 🚀 새로운 제품 이벤트 로그 생성
     * @param productEventLog 저장할 제품 이벤트 로그 객체
     * @return 저장된 제품 이벤트 로그 객체 반환
     */
    @PostMapping
    public ResponseEntity<ProductEventLog> createLog(@RequestBody ProductEventLog productEventLog) {
        return ResponseEntity.ok(productEventLogService.saveLog(productEventLog));
    }

    /**
     * 🚀 ID로 제품 이벤트 로그 삭제
     * @param id 삭제할 제품 이벤트 로그 ID
     * @return HTTP 204 No Content 응답 반환
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        productEventLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🚀 특정 기간 동안의 제품 이벤트 로그 조회
     * @param startTime 조회할 시작 날짜 (yyyy-MM-dd HH:mm:ss 형식)
     * @param endTime 조회할 종료 날짜 (yyyy-MM-dd HH:mm:ss 형식)
     * @return 해당 기간 동안의 제품 이벤트 로그 목록 반환
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<ProductEventLog>> getLogsByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {

        Date startDate = parseDate(startTime); // 문자열을 Date로 변환
        Date endDate = parseDate(endTime); // 문자열을 Date로 변환

        return ResponseEntity.ok(productEventLogService.findLogsByTimeRange(startDate, endDate));
    }

    /**
     * ✅ 문자열을 Date 타입으로 변환하는 유틸리티 메서드
     * @param dateStr 변환할 날짜 문자열
     * @return 변환된 Date 객체 (형식이 잘못된 경우 null 반환)
     */
    private Date parseDate(String dateStr) {
        try {
            return dateStr != null && !dateStr.isEmpty()
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr) // 지정된 형식으로 변환
                    : null;
        } catch (ParseException e) {
            return null;
        }
    }
}
