package com.newmeta.service; // 해당 서비스 클래스가 속한 패키지를 선언

import java.util.List; // 리스트 데이터를 처리하기 위한 라이브러리
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션

import com.newmeta.domain.Product; // 제품 엔티티 클래스 임포트
import com.newmeta.domain.ProductEventLog; // 제품 이벤트 로그 엔티티 클래스 임포트
import com.newmeta.persistence.ProductEventLogRepository; // 제품 이벤트 로그 저장소 인터페이스 임포트
import com.newmeta.persistence.ProductRepository; // 제품 저장소 인터페이스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성
import lombok.extern.slf4j.Slf4j; // 로깅을 위한 Lombok 어노테이션

/**
 * 📌 제품 데이터 관리 서비스
 */
@Slf4j // 로깅 기능 자동 추가
@Service // Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동 생성
public class ProductService {

    private final ProductRepository productRepo; // 제품 정보를 관리하는 JPA 저장소

    /**
     * 🚀 모든 제품 조회
     * @return 데이터베이스에 저장된 모든 제품 목록 반환
     */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    /**
     * 🚀 제품 저장
     * @param product 저장할 제품 객체
     * @return 저장된 제품 객체 반환
     */
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    /**
     * 🚀 특정 제품 조회
     * @param epcId 조회할 제품의 EPC 코드
     * @return 해당 EPC 코드에 해당하는 제품 객체 반환
     * @throws RuntimeException 제품이 존재하지 않을 경우 예외 발생
     */
    public ResponseEntity<Product> getProductById(String epcId) {
        Optional<Product> product = productRepo.findById(epcId);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * 🚀 제품 수정
     * @param epcId 수정할 제품의 EPC 코드
     * @param updatedProduct 수정할 제품 정보
     * @return 수정된 제품 객체 반환
     * @throws RuntimeException 제품이 존재하지 않을 경우 예외 발생
     */
    public Product updateProduct(String epcId, Product updatedProduct) {
        // 기존 제품 찾기
        Product product = productRepo.findById(epcId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 수정할 내용 적용
        if (updatedProduct.getProductName() != null) {
            product.setProductName(updatedProduct.getProductName());
        }

        // 저장 후 반환
        return productRepo.save(product);
    }

    /**
     * 🚀 제품 삭제
     * @param epcId 삭제할 제품의 EPC 코드
     */
    public void deleteProduct(String epcId) {
        productRepo.deleteById(epcId);
    }

    /**
     * 🚀 제품이 존재하지 않으면 저장하고, 존재하면 기존 값을 반환
     * @param epcCode 제품의 EPC 코드
     * @param productName 제품명
     * @return 저장된 또는 기존 제품 객체 반환
     */
    public Product saveIfNotExists(String epcCode, String productName) {
        return productRepo.findById(epcCode)
                .orElseGet(() -> productRepo.save(new Product(epcCode, productName)));
    }
}
