package com.newmeta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
	@Id
	@Column(name = "epc_id")
	private String epcId; // EPC 코드
	private Long productSerial; // 제품 일련번호
	private String productName; // 제품명
}
