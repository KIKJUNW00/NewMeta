package com.newmeta.controller; // 해당 컨트롤러 클래스가 속한 패키지를 선언

import com.newmeta.domain.Review; // 리뷰 엔티티 클래스 임포트
import com.newmeta.service.ReviewService; // 리뷰 서비스 클래스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성
import org.springframework.http.ResponseEntity; // HTTP 응답을 처리하기 위한 ResponseEntity 임포트
import org.springframework.web.bind.annotation.*; // Spring Web 관련 어노테이션 임포트

import java.util.List; // 리스트 데이터를 다루기 위한 라이브러리

/**
 * 📌 리뷰 관리 컨트롤러
 * ✅ 리뷰 저장 및 조회 기능 제공
 */
@RestController // RESTful API 컨트롤러로 등록
@RequestMapping("/reviews") // 모든 API 엔드포인트가 `/reviews`로 시작하도록 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동 생성
public class ReviewController {

    private final ReviewService reviewService; // 리뷰 데이터 관리 서비스

    /**
     * 🚀 리뷰 분석 및 저장 API
     * @param review 저장할 리뷰 객체 (리뷰 내용, AI 예측 결과 포함)
     * @return 저장된 리뷰 객체 반환
     */
    @PostMapping
    public ResponseEntity<Review> analyzeReview(@RequestBody Review review) {
        Review savedReview = reviewService.analyzeAndSaveReview(review.getReview(), review.getPrediction());
        return ResponseEntity.ok(savedReview);
    }

    /**
     * 🚀 모든 리뷰 정보 가져오기
     * @return 데이터베이스에 저장된 모든 리뷰 목록 반환
     */
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> review = reviewService.getAllReview(); // 서비스 계층에서 데이터 조회
        return ResponseEntity.ok(review); // 조회된 데이터를 응답으로 반환
    }
}
