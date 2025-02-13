package com.newmeta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.service.ReportedService;

import lombok.extern.slf4j.Slf4j;

/**
 * 하나의 컨트롤러(ReportedController) 내에 기존 원본 AnomalyDetectionService 로직을 모두 구현. 1)
 * 이벤트 생성 및 DB 저장 2) (임계치 없이) 데이터가 1개 이상이면 이상치 탐지 로직 수행 3) fastapi(AI) 호출은 "국내산
 * 10개, 수입산 8개" 이상일 때만 수행. 4) domestic/imported 첫 이벤트 검사, 직접 판매 이벤트, 허가되지 않은
 * 이벤트, 순서 오류 등 모두 포함. 5) 이상 발생 시 anomalyLog 저장, 웹소켓 알림 등까지 구현.
 */
@RestController
@Slf4j
public class ReportedController {

	@Autowired
	private ReportedService reportedService;
	
	@PostMapping("/report")
	public void reported(@RequestBody ProductEventLogDTO dto) {
		// ★ 기존 코드 로직을 서비스로 분리
		reportedService.reported(dto);
	}
}
