package com.newmeta.cache; // 패키지 경로 설정: 캐시 관련 클래스는 'cache' 패키지에 위치

import com.newmeta.domain.ProductEventLog; // 제품 이벤트 로그 엔티티 클래스 임포트
import lombok.extern.slf4j.Slf4j; // 로깅 기능을 위한 Lombok 어노테이션 사용
import org.springframework.stereotype.Component; // Spring의 컴포넌트 스캔을 위한 어노테이션

import java.util.*;

/**
 * 🚀 EPC 별 이벤트 히스토리를 저장하는 캐시
 * - EPC 코드(전자 제품 코드)를 기준으로 제품 이동 이벤트를 캐싱하여 빠른 조회 가능
 * - 이벤트 히스토리를 메모리에 저장하여 데이터베이스 접근을 최소화
 * - 동시성 이슈를 방지하기 위해 `synchronized` 적용
 */
@Component // ✅ Spring Bean으로 등록 (싱글톤 객체로 관리됨)
@Slf4j // ✅ 로깅 기능 추가 (Lombok 사용)
public class EventHistoryCache {

    // ✅ EPC 코드별 이벤트 리스트 저장 (임시 메모리 저장)
    // - Key: EPC 코드 (String)
    // - Value: 해당 EPC 코드에 대한 이벤트 목록 (List<ProductEventLog>)
    private final Map<String, List<ProductEventLog>> eventCache = new HashMap<>();

    /**
     * 🚀 이벤트 추가 (EPC 코드 기준으로 저장)
     * - 특정 제품의 이벤트를 캐시에 저장
     * - 동기화 처리 (`synchronized`)로 **멀티 스레드 환경에서도 안전하게 추가 가능**
     * - 만약 동일한 EPC 코드가 이미 존재하면 기존 리스트에 추가
     * 
     * @param eventLog 저장할 제품 이벤트 로그 객체
     */
    public synchronized void addEvent(ProductEventLog eventLog) {
        // ✅ 유효성 검사: null 값이 들어오면 저장하지 않음
        if (eventLog == null || eventLog.getProduct() == null) {
            log.warn("⚠️ 이벤트 추가 실패: 이벤트 또는 제품 정보가 NULL");
            return; // 이벤트 추가 중단
        }

        // ✅ EPC 코드 추출
        String epcCode = eventLog.getProduct().getEpcCode();

        // ✅ 캐시에 EPC 코드가 존재하지 않으면 새로운 리스트 생성 후 이벤트 추가
        // `computeIfAbsent` 사용: 해당 키가 없으면 새 리스트를 생성하여 저장
        eventCache.computeIfAbsent(epcCode, k -> new ArrayList<>()).add(eventLog);

        // ✅ 저장 완료 로그 출력
        log.info("✅ 이벤트 추가: EPC [{}], 이벤트 [{}]", epcCode, eventLog.getEvent().getEventType());
    }

    /**
     * 🚀 특정 EPC 코드의 이벤트 목록 반환
     * - EPC 코드에 해당하는 이벤트 목록을 가져옴
     * - 만약 EPC 코드가 존재하지 않으면 빈 리스트 반환 (NPE 방지)
     * - 동기화 처리 (`synchronized`)로 **멀티 스레드 환경에서도 안전하게 조회 가능**
     * 
     * @param epcCode 조회할 EPC 코드
     * @return 해당 EPC 코드의 이벤트 로그 목록 (없으면 빈 리스트 반환)
     */
    public synchronized List<ProductEventLog> getEventsByEpc(String epcCode) {
        return eventCache.getOrDefault(epcCode, Collections.emptyList());
    }

    /**
     * 🚀 특정 EPC 코드의 이벤트 기록 제거
     * - 해당 EPC 코드에 대한 모든 이벤트 데이터를 캐시에서 삭제
     * - **SCM(공급망 관리) 프로세스 완료 후, 불필요한 캐시 데이터 제거 용도**
     * 
     * @param epcCode 삭제할 EPC 코드
     */
    public synchronized void removeEventHistory(String epcCode) {
        // ✅ 해당 EPC 코드가 존재하면 삭제
        if (eventCache.containsKey(epcCode)) {
            eventCache.remove(epcCode);
            log.info("🗑 이벤트 기록 삭제: EPC [{}]", epcCode);
        }
    }

    /**
     * 🚀 전체 캐시 데이터 확인 (디버깅용)
     * - 현재 캐시에 저장된 EPC 이벤트 개수를 로그로 출력
     * - 각 EPC 코드별 저장된 이벤트 개수 확인 가능
     */
    public synchronized void printCacheStatus() {
        // ✅ 캐시의 총 EPC 개수 출력
        log.info("📊 현재 저장된 EPC 이벤트 수: {}", eventCache.size());

        // ✅ 각 EPC 코드별 저장된 이벤트 개수 출력
        eventCache.forEach((epc, events) -> 
            log.info("📌 EPC [{}]: {} 개 이벤트 기록됨", epc, events.size()));
    }
}
