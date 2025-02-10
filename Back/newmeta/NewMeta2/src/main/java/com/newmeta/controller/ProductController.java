package com.newmeta.controller; // 해당 컨트롤러 클래스가 속한 패키지를 선언

import java.util.List; // 리스트 데이터를 다루기 위한 라이브러리

import org.springframework.http.ResponseEntity; // HTTP 응답을 처리하기 위한 ResponseEntity 임포트
// Spring Web 관련 어노테이션 임포트
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.Product; // 제품 엔티티 클래스 임포트
import com.newmeta.service.ProductService; // 제품 서비스 클래스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성

/**
 * 📌 제품 관리 컨트롤러
 * ✅ 제품 CRUD (생성, 조회, 수정, 삭제) 및 이동 경로 조회
 */
@RestController // RESTful API 컨트롤러로 등록
@RequestMapping("/product") // 모든 API 엔드포인트가 `/product`로 시작하도록 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동 생성
public class ProductController {

    private final ProductService productService; // 제품 데이터 관리 서비스


    /**
     * 🚀 모든 제품 조회
     * @return 데이터베이스에 저장된 모든 제품 목록 반환
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * 🚀 새로운 제품 등록
     * @param product 저장할 제품 객체
     * @return 저장된 제품 객체 반환
     */
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    /**
     * 🚀 특정 제품 조회 (EPC ID 기준)
     * @param epcId 조회할 제품의 EPC 코드
     * @return 해당 EPC 코드에 해당하는 제품 객체 반환
     */
    @GetMapping("/{epcId}")
    public ResponseEntity<ResponseEntity<Product>> getProductById(@PathVariable String epcId) {
        return ResponseEntity.ok(productService.getProductById(epcId));
    }

    /**
     * 🚀 제품 정보 수정
     * @param epcId 수정할 제품의 EPC 코드
     * @param updatedProduct 수정할 제품 정보
     * @return 수정된 제품 객체 반환
     */
    @PutMapping("/{epcId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String epcId, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(epcId, updatedProduct));
    }

    /**
     * 🚀 특정 제품 삭제
     * @param epcId 삭제할 제품의 EPC 코드
     * @return HTTP 204 No Content 응답 반환
     */
    @DeleteMapping("/{epcId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String epcId) {
        productService.deleteProduct(epcId);
        return ResponseEntity.noContent().build();
    }
}
