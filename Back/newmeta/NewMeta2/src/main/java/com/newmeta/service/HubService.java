package com.newmeta.service; // 📌 해당 서비스 클래스가 속한 패키지를 선언

// ✅ JPA 저장소 및 엔티티 클래스 임포트
import com.newmeta.domain.Hub; // 허브 엔티티 클래스 임포트
import com.newmeta.persistence.HubRepository; // 허브 데이터를 관리하는 JPA 저장소 인터페이스 임포트

// ✅ Spring 및 Lombok 관련 어노테이션 임포트
import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성
import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션

/**
 * 📌 **허브 데이터 관리 서비스**
 * ✅ 허브(Hub) 데이터를 저장하고 관리하는 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service // ✅ Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자를 Lombok이 자동 생성
public class HubService {

    // ✅ Hub 엔티티를 관리하는 JPA 저장소
    private final HubRepository hubRepo;

    /**
     * 🚀 **허브가 존재하지 않으면 저장하고, 존재하면 기존 값을 반환**
     * ✅ 허브 이름, 위도(latitude), 경도(longitude)를 기준으로 기존 데이터를 조회 후, 없으면 새로 저장
     * @param hubName 저장할 허브 이름
     * @param latitude 허브의 위도
     * @param longitude 허브의 경도
     * @return 저장된 Hub 객체
     */
    public Hub saveIfNotExists(String hubName, Double latitude, Double longitude) {
        return hubRepo.findByHubTypeAndLatitudeAndLongitude(hubName, latitude, longitude) // ✅ 기존 허브 존재 여부 확인
                .orElseGet(() -> hubRepo.save(Hub.builder() // ✅ 존재하지 않으면 새 허브 저장
                        .hubType(hubName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build()));
    }
}
