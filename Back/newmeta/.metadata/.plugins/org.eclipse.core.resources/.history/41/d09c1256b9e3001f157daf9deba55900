package com.newmeta.service; // 해당 서비스 클래스가 속한 패키지를 선언

import java.util.Optional; // Optional을 사용하여 null 처리를 보다 안전하게 수행

import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션

import com.newmeta.domain.Event; // 이벤트 엔티티 클래스 임포트
import com.newmeta.persistence.EventRepository; // 이벤트 데이터를 관리하는 JPA 저장소 인터페이스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성

/**
 * 📌 이벤트 데이터 관리 서비스
 */
@Service // Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동 생성
public class EventService {

    private final EventRepository eventRepo; // Event 엔터티를 관리하는 JPA 저장소

    /**
     * 🚀 이벤트가 존재하지 않으면 저장하고, 존재하면 기존 값을 반환
     * @param eventType 저장할 이벤트 유형
     * @return 저장된 Event 객체
     */
    public Event saveIfNotExists(String eventType) {
        return eventRepo.findByEventType(eventType) // 해당 eventType이 존재하는지 조회
                .orElseGet(() -> eventRepo.save(Event.builder() // 존재하지 않으면 새로 저장
                        .eventType(eventType)
                        .build()));
    }

    /**
     * 🚀 특정 이벤트 유형으로 이벤트 조회
     * @param eventType 조회할 이벤트 유형
     * @return Optional<Event> 객체 반환 (존재하지 않을 경우 empty)
     */
    public Optional<Event> findByEventType(String eventType) {
        return eventRepo.findByEventType(eventType);
    }
}
