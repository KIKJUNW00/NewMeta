package com.newmeta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.Product;
import com.newmeta.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 모든 제품 조회
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 제품 저장
    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // 특정 제품 조회
    @GetMapping("/{epcId}")
    public Product getProductById(@PathVariable String epcId) {
        return productService.getProductById(epcId);
    }

    // 제품 수정
    @PutMapping("/{epcId}")
    public Product updateProduct(@PathVariable String epcId, @RequestBody Product updatedProduct) {
        return productService.updateProduct(epcId, updatedProduct);
    }

    // 제품 삭제
    @DeleteMapping("/{epcId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String epcId) {
        productService.deleteProduct(epcId);
        return ResponseEntity.noContent().build();
    }
}