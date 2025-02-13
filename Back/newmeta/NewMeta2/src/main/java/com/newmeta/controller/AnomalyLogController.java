package com.newmeta.controller; // 📌 해당 컨트롤러 클래스가 속한 패키지를 선언

import java.util.List; // 리스트 데이터를 다루기 위한 라이브러리

// ✅ Spring 관련 라이브러리 임포트
import org.springframework.data.domain.Page; // 페이징 처리를 위한 Page 객체
import org.springframework.data.domain.Pageable; // 페이징 요청 정보를 담는 Pageable 객체
import org.springframework.http.ResponseEntity; // HTTP 응답을 처리하는 ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ✅ 도메인 엔티티 및 서비스 클래스 임포트
import com.newmeta.domain.AnomalyLog; // 이상 탐지 로그 엔티티 클래스
import com.newmeta.domain.dto.AnomalyDTO;
import com.newmeta.service.AnomalyLogService; // 이상 탐지 서비스 클래스
// import com.newmeta.service.WebSocketService; // 실시간 WebSocket 알림 서비스 (추후 활성화 가능)

import lombok.RequiredArgsConstructor; // Lombok: final 필드 자동 생성자 주입
import lombok.extern.slf4j.Slf4j; // Lombok: 로깅 기능 추가

/**
 * 📌 이상 탐지 컨트롤러
 * ✅ 이상 탐지 데이터 조회, 필터링, 삭제 기능을 제공
 */
@Slf4j // ✅ 로깅 기능 자동 추가
@RestController // ✅ RESTful API 컨트롤러로 등록
@RequestMapping("/anomalies") // ✅ 엔드포인트 설정 (`/api` 제외)
@RequiredArgsConstructor // ✅ Lombok: final 필드 자동 생성자 추가
public class AnomalyLogController {

    // ✅ 이상 탐지 데이터 관리 서비스
    private final AnomalyLogService anomalyLogService;

//    private final WebSocketService webSocketService; // ✅ 실시간 WebSocket 알림 서비스 (필요 시 활성화 가능)

    @GetMapping("/paged")
    public ResponseEntity<Page<AnomalyDTO>> getPagedAnomalyDetails(Pageable pageable) {
        Page<AnomalyDTO> pagedAnomalies = anomalyLogService.getPagedAnomalyDetails(pageable);
        return ResponseEntity.ok(pagedAnomalies);
    }

    /**
     * 🚀 **특정 EPC 코드로 이상 탐지 데이터 조회**
     * ✅ 특정 EPC 코드에 대한 이상 탐지 로그를 검색하여 반환
     * @param epcCode 조회할 EPC 코드
     * @return 해당 EPC 코드와 관련된 이상 탐지 로그 목록
     */
    @GetMapping("/{epcCode}")
    public ResponseEntity<List<AnomalyLog>> getAnomaliesByEpc(@PathVariable String epcCode) {
        return ResponseEntity.ok(anomalyLogService.getAnomaliesByEpc(epcCode));
    }

    /**
     * 🚀 **이상 탐지 데이터 삭제**
     * ✅ 특정 ID의 이상 탐지 데이터를 삭제
     * @param anomalyId 삭제할 이상 탐지 로그의 ID
     * @return HTTP 204 No Content 응답 반환
     */
    @DeleteMapping("/{anomalyId}")
    public ResponseEntity<Void> deleteAnomaly(@PathVariable Long anomalyId) {
        anomalyLogService.deleteAnomaly(anomalyId);
        return ResponseEntity.noContent().build();
    }
}
