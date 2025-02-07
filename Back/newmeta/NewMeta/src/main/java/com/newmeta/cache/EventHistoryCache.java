package com.newmeta.cache;

import com.newmeta.domain.ProductEventLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 📌 EPC별 이벤트 히스토리를 임시로 저장하는 캐시
 * - 이상 탐지시 최근 이벤트를 빠르게 조회
 * - 개선사항: 동시성 대응(ConcurrentHashMap) + TTL 정책(오래된 EPC 자동 제거)
 */
@Component
@Slf4j
public class EventHistoryCache {

    // 개선사항: 멀티 스레드 + 고부하 시 ConcurrentHashMap + 병렬 리스트 구조 고려
    private final Map<String, List<ProductEventLog>> eventCache = new HashMap<>();

    /**
     * 📌 이벤트 추가
     */
    public synchronized void addEvent(ProductEventLog eventLog) {
        if (eventLog == null || eventLog.getProduct() == null) {
            log.warn("⚠️ [addEvent] 이벤트 또는 제품정보 NULL");
            return;
        }
        String epc = eventLog.getProduct().getEpcCode();
        eventCache.computeIfAbsent(epc, k -> new ArrayList<>()).add(eventLog);

        log.debug("✅ 이벤트 추가: EPC={} | eventType={}", epc, eventLog.getEvent().getEventType());
    }

    /**
     * 📌 EPC별 이벤트 리스트 조회
     */
    public synchronized List<ProductEventLog> getEventsByEpc(String epcCode) {
        return eventCache.getOrDefault(epcCode, Collections.emptyList());
    }

    /**
     * 📌 EPC 이력 제거
     * - 이상 탐지 끝나거나 정상 프로세스 완료 시점에 호출
     */
    public synchronized void removeEventHistory(String epcCode) {
        if (eventCache.containsKey(epcCode)) {
            eventCache.remove(epcCode);
            log.debug("🗑 캐시 제거: EPC={}", epcCode);
        }
    }

    /**
     * 📌 캐시 상태 출력(디버깅)
     */
    public synchronized void printCacheStatus() {
        log.info("📊 EventHistoryCache size: {}", eventCache.size());
        eventCache.forEach((epc, list) ->
                log.info(" - EPC={} | events={}", epc, list.size())
        );
    }
}
