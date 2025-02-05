package com.newmeta.service; // 📌 해당 서비스 클래스가 속한 패키지를 선언

// ✅ 필수 라이브러리 및 의존성 임포트
import java.util.List; // 리스트 데이터를 다루기 위한 라이브러리
import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션
import com.newmeta.domain.Review; // 리뷰 엔티티 클래스 임포트
import com.newmeta.persistence.ReviewRepository; // 리뷰 데이터를 관리하는 JPA 저장소 인터페이스 임포트
import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성

/**
 * 📌 **리뷰 데이터 관리 서비스**
 * ✅ 사용자의 리뷰 데이터를 저장하고, AI 분석 결과를 함께 관리하는 서비스
 */
@Service // ✅ Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자를 Lombok이 자동 생성
public class ReviewService {

    // ✅ JPA Repository 의존성 주입 (리뷰 데이터베이스 연동)
    private final ReviewRepository reviewRepository;

    /**
     * 🚀 **리뷰를 AI로 분석 후 저장**
     * ✅ 사용자가 입력한 리뷰를 저장하며, AI의 긍정/부정 분석 결과를 함께 기록
     * @param review 사용자가 작성한 리뷰 텍스트
     * @param prediction AI 분석 결과 (긍정/부정 등)
     * @return 저장된 리뷰 객체 반환
     */
    public Review analyzeAndSaveReview(String review, String prediction) {
        // ✅ 데이터베이스에 저장할 Review 엔티티 생성
        Review reviewEntity = new Review();
        reviewEntity.setReview(review); // 원본 리뷰 저장
        reviewEntity.setPrediction(prediction); // AI 예측 결과 저장
        return reviewRepository.save(reviewEntity); // ✅ 저장 후 반환
    }

    /**
     * 🚀 **모든 리뷰 조회**
     * ✅ 데이터베이스에 저장된 모든 리뷰 목록을 조회하여 반환
     * @return `List<Review>` (리뷰 리스트 반환)
     */
    public List<Review> getAllReview() {
        return reviewRepository.findAll();
    }
}
