package com.newmeta.service; // 📌 해당 서비스 클래스가 속한 패키지를 선언

// ✅ 필수 라이브러리 및 의존성 임포트
import java.util.List; // 리스트 데이터를 처리하기 위한 라이브러리
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service; // Spring의 서비스 계층을 나타내는 어노테이션

import com.newmeta.domain.Product; // 제품 엔티티 클래스 임포트
import com.newmeta.persistence.ProductRepository; // 제품 저장소 인터페이스 임포트

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성
import lombok.extern.slf4j.Slf4j; // 로깅을 위한 Lombok 어노테이션

/**
 * 📌 **제품 데이터 관리 서비스**
 * ✅ 제품 정보를 저장, 조회, 수정, 삭제하는 서비스 계층
 */
@Slf4j // ✅ 로깅 기능 자동 추가
@Service // ✅ Spring의 Service 컴포넌트로 등록하여 관리되도록 설정
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자를 Lombok이 자동 생성
public class ProductService {

    // ✅ Product 엔티티를 관리하는 JPA 저장소
    private final ProductRepository productRepo;

    /**
     * 🚀 **모든 제품 조회**
     * ✅ 데이터베이스에서 저장된 모든 제품 목록을 반환
     * @return `List<Product>` 제품 리스트 반환
     */
    public List<Product> getAllProducts() {
        log.info("📡 [제품 목록 조회 요청]");
        return productRepo.findAll();
    }

    /**
     * 🚀 **제품 저장**
     * ✅ 새로운 제품을 저장하고 반환
     * @param product 저장할 제품 객체
     * @return 저장된 제품 객체 반환
     */
    public Product saveProduct(Product product) {
        log.info("🆕 [제품 저장 요청] EPC 코드: {}, 제품명: {}", product.getEpcCode(), product.getProductName());
        return productRepo.save(product);
    }

    /**
     * 🚀 **특정 제품 조회**
     * ✅ EPC 코드 기준으로 제품을 조회 (없을 경우 404 반환)
     * @param epcId 조회할 제품의 EPC 코드
     * @return `ResponseEntity<Product>` (제품이 존재하면 200 OK, 없으면 404 Not Found)
     */
    public ResponseEntity<Product> getProductById(String epcId) {
        log.info("🔍 [제품 조회 요청] EPC 코드: {}", epcId);
        Optional<Product> product = productRepo.findById(epcId);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 🚀 **제품 수정**
     * ✅ 기존 제품 정보를 수정하고 반환
     * @param epcId 수정할 제품의 EPC 코드
     * @param updatedProduct 수정할 제품 정보
     * @return 수정된 제품 객체 반환
     * @throws RuntimeException 제품이 존재하지 않을 경우 예외 발생
     */
    public Product updateProduct(String epcId, Product updatedProduct) {
        log.info("✏️ [제품 수정 요청] EPC 코드: {}", epcId);

        // ✅ 기존 제품 찾기
        Product product = productRepo.findById(epcId)
                .orElseThrow(() -> new RuntimeException("⚠️ Product not found: EPC 코드 " + epcId));

        // ✅ 제품명 변경 (값이 존재하는 경우)
        if (updatedProduct.getProductName() != null) {
            log.info("🔄 제품명 변경: {} -> {}", product.getProductName(), updatedProduct.getProductName());
            product.setProductName(updatedProduct.getProductName());
        }

        // ✅ 변경된 내용 저장 후 반환
        return productRepo.save(product);
    }

    /**
     * 🚀 **제품 삭제**
     * ✅ 특정 EPC 코드를 가진 제품을 삭제
     * @param epcId 삭제할 제품의 EPC 코드
     */
    public void deleteProduct(String epcId) {
        log.info("🗑️ [제품 삭제 요청] EPC 코드: {}", epcId);
        productRepo.deleteById(epcId);
    }

}
