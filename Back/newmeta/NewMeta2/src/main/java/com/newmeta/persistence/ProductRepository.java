package com.newmeta.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newmeta.domain.Hub;
import com.newmeta.domain.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
	 Optional<Product> findByEpcCode(String epcCode);



}
