package com.newmeta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newmeta.domain.Product;
import com.newmeta.persistence.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    // 모든 제품 조회
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // 제품 저장
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    // 특정 제품 조회
    public Product getProductById(String epcId) {
        return productRepo.findById(epcId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    

    // 제품 수정
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

    // 제품 삭제
    public void deleteProduct(String epcId) {
    	productRepo.deleteById(epcId);
    }
}
