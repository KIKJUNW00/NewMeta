package com.newmeta.domain;

import java.util.Date; // 기존: Date 사용
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEventLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productEventLogId; // 기본키

	@Column(name = "event_time", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP) // 기존: Date 사용
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date eventTime; // 이벤트 발생 시간

	@Column(name = "is_anomaly")
	private Boolean isAnomaly; // 이상치 여부
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "epc_code")
	@JsonIgnore
	private Product product;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "event_id")
	@JsonIgnore
	private Event event;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "hub_type")
	@JsonIgnore
	private Hub hub;


	

	
}
