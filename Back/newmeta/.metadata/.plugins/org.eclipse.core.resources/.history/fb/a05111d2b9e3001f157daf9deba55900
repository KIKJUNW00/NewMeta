package com.newmeta.service; // 해당 서비스 클래스가 속한 패키지를 선언

import com.newmeta.domain.Hub; // 허브 엔티티 클래스 임포트
import com.newmeta.persistence.HubRepository; // 허브 데이터를 관리하는 JPA 저장소 인터페이스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성
import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션

/**
 * 📌 허브 데이터 관리 서비스
 */
@Service // Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동 생성
public class HubService {

    private final HubRepository hubRepo; // Hub 엔터티를 관리하는 JPA 저장소

    /**
     * 🚀 허브가 존재하지 않으면 저장하고, 존재하면 기존 값을 반환
     * @param hubName 저장할 허브 이름
     * @param latitude 허브의 위도
     * @param longitude 허브의 경도
     * @return 저장된 Hub 객체
     */
    public Hub saveIfNotExists(String hubName, Double latitude, Double longitude) {
        return hubRepo.findByHubNameAndLatitudeAndLongitude(hubName, latitude, longitude) // 허브 존재 여부 확인
                .orElseGet(() -> hubRepo.save(Hub.builder() // 존재하지 않으면 새 허브 저장
                        .hubName(hubName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build()));
    }
}
