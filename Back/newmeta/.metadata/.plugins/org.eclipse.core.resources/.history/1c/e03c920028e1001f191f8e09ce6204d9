package com.newmeta.controller;

import com.newmeta.domain.Review;
import com.newmeta.service.ReviewService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 분석 및 저장 API
    @PostMapping
    public ResponseEntity<Review> analyzeReview(@RequestBody Review review) {
    	Review savedReview = reviewService.analyzeAndSaveReview(review.getReview(), review.getPrediction());
        return ResponseEntity.ok(savedReview);
    }
    
    // 모든 리뷰 정보 가져오기
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> review = reviewService.getAllReview(); // 서비스 계층에서 데이터 조회
        return ResponseEntity.ok(review); // 조회된 데이터를 응답으로 반환
    }
    

}