package com.newmeta.event; // 해당 이벤트 클래스가 속한 패키지를 선언

import lombok.Getter; // Lombok의 Getter 어노테이션을 사용하여 모든 필드의 Getter 메서드를 자동 생성
import org.springframework.context.ApplicationEvent; // Spring의 이벤트 시스템에서 사용하는 ApplicationEvent 클래스를 임포트

import java.util.Map; // scmData를 저장하기 위해 Map 컬렉션을 사용

@Getter // 모든 필드에 대한 Getter 메서드를 자동 생성
public class SCMDataUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L; // ✅ 직렬화를 위한 버전 ID 추가 (객체 직렬화 시 호환성을 유지하기 위함)

    private final Object scmData; // SCM 관련 데이터를 저장하는 필드 (이벤트 발생 시 전달될 데이터)

    /**
     * SCMDataUpdateEvent 생성자
     * @param source 이벤트를 발생시키는 객체 (Spring 이벤트 시스템에서 필수)
     * @param scmData SCM 데이터를 포함하는 Map 객체
     */
    public SCMDataUpdateEvent(Object source, Map<String, Object> scmData) {
        super(source); // ApplicationEvent 부모 생성자를 호출하여 이벤트의 출처(Source) 설정
        this.scmData = scmData; // 이벤트와 함께 전달될 SCM 데이터 저장
    }
}
