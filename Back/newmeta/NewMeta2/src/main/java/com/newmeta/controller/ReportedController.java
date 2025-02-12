package com.newmeta.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.newmeta.domain.AnomalyLog;
import com.newmeta.domain.Event;
import com.newmeta.domain.Hub;
import com.newmeta.domain.Product;
import com.newmeta.domain.ProductEventLog;
import com.newmeta.domain.dto.AnomalyDTO;
import com.newmeta.domain.dto.PredictionResultDTO;
import com.newmeta.domain.dto.ProductEventLogDTO;
import com.newmeta.persistence.AnomalyLogRepository;
import com.newmeta.persistence.EventRepository;
import com.newmeta.persistence.HubRepository;
import com.newmeta.persistence.ProductEventLogRepository;
import com.newmeta.persistence.ProductRepository;
import com.newmeta.service.FastAPIService;
import com.newmeta.service.WebSocketService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ReportedController {

	@Autowired
	ProductEventLogRepository productEventLogRepo;

	@Autowired
	ProductRepository productRepo;

	@Autowired
	EventRepository eventRepository;

	@Autowired
	HubRepository hubRepository;

	@Autowired
	AnomalyLogRepository anomalyLogRepository;

	@Autowired
	FastAPIService fastAPIService;

	@Autowired
	WebSocketService webSocketService;

	private static final String DOMESTIC_PREFIX = "001.880";
	private static final int DOMESTIC_AI_LIMIT = 10;
	private static final int IMPORTED_AI_LIMIT = 8;

	@PostMapping("/report")
	public void reported(@RequestBody ProductEventLogDTO dto) {
		Product product = productRepo.findById(dto.getEpcCode()).orElse(null);
		if (product == null) {
			product = Product.builder().epcCode(dto.getEpcCode()).productName(dto.getProductName())
					.productSerial(dto.getProductSerial()).build();
		}

		Event event = eventRepository.findByEventType(dto.getEventType()).orElse(null);
		if (event == null) {
			event = Event.builder().eventType(dto.getEventType()).build();
		}

		Hub hub = hubRepository.findById(dto.getHubType()).orElse(null);
		if (hub == null) {
			hub = Hub.builder().hubType(dto.getHubType()).latitude(dto.getLatitude()).longitude(dto.getLongitude())
					.build();
		}

		ProductEventLog productEventLog = ProductEventLog.builder().product(product).eventTime(dto.getEventTime())
				.event(event).hub(hub).isAnomaly(false).build();
		productEventLogRepo.save(productEventLog);
		log.info("[Report Controller] 새 이벤트 저장. EPC={}, EventType={} ", dto.getEpcCode(), dto.getEventType());

		List<ProductEventLog> events = productEventLogRepo.findAllByProduct_EpcCodeOrderByProductEventLogIdAsc(dto.getEpcCode());
		log.info("[보고된 이벤트 수] EPC={} | count={} ", dto.getEpcCode(), events.size());

		if (!events.isEmpty()) {
			detectAnomalies(dto.getEpcCode(), events);
		} else {
			log.warn("[이상치 검사 스킵] 이벤트가 0개");
		}

		List<ProductEventLog> last10 = productEventLogRepo.findTop10ByProduct_EpcCodeOrderByProductEventLogIdDesc(dto.getEpcCode());
		log.info("[최근 10개 로그 출력] EPC={} ", dto.getEpcCode());
		for (ProductEventLog logItem : last10) {
			log.info(" - {}", logItem);
		}
	}

	private void detectAnomalies(String epcCode, List<ProductEventLog> events) {
		log.info("[detectAnomalies] EPC={} | 이벤트수={} ", epcCode, events.size());

		Optional<ProductEventLog> domesticIssue = findDomesticFirstEventIssue(epcCode, events);
		if (domesticIssue.isPresent()) {
			saveAnomalyLog(domesticIssue.get(), "위조", "commissioning 이후 첫 이벤트가 올바르지 않음");
			return;
		}

		Optional<ProductEventLog> importedIssue = findImportedFirstEventIssue(epcCode, events);
		if (importedIssue.isPresent()) {
			saveAnomalyLog(importedIssue.get(), "밀수", "custom_inbound 이후 첫 이벤트가 잘못됨");
			return;
		}

		Optional<ProductEventLog> directSellEvt = findDirectSellEvent(events);
		if (directSellEvt.isPresent()) {
			saveAnomalyLog(directSellEvt.get(), "불법 유통", "허브 이동 없이 판매 발생");
			return;
		}

		Optional<ProductEventLog> unauthorizedEvt = findUnauthorizedEvent(events);
		if (unauthorizedEvt.isPresent()) {
			saveAnomalyLog(unauthorizedEvt.get(), "이상 이벤트 발생", "허용되지 않은 이벤트 탐지됨");
			return;
		}
		if (events.size() >= 11) {
			boolean foundSellEvent = false;
			for (ProductEventLog event : events) {
				if ("stock_outbound(Sell)".equals(event.getEvent().getEventType())) {
					foundSellEvent = true;
				} else if (foundSellEvent) {
					saveAnomalyLog(event, "EPC 코드 중복 이상치", "stock_outbound(Sell) 이후 동일 EPC 코드 발생");
					return;
				}
			}
		}

		Optional<ProductEventLog> seqErrorEvt = findSequenceErrorEvent(epcCode, events);
		if (seqErrorEvt.isPresent()) {
			saveAnomalyLog(seqErrorEvt.get(), "이벤트 순서 오류", "정상 흐름 불일치");
			return;
		}


		boolean isDomestic = isDomesticProduct(epcCode);
		if (isDomestic && events.size() >= DOMESTIC_AI_LIMIT) {  // ✅ 국내산만 AI 호출
		    List<PredictionResultDTO> aiResults = fastAPIService.detectAnomalies(events);
		    if (!aiResults.isEmpty()) {
		        for (PredictionResultDTO result : aiResults) {
		            boolean isAnomaly = result.isAnomaly();
		            String anomalyEpc = result.getEpcCode();

		            List<ProductEventLog> matchingEvents = events.stream()
		                .filter(e -> e.getProduct().getEpcCode().equals(anomalyEpc))  // ✅ EPC 코드가 일치하는 이벤트 필터링
		                .toList();

		            if (matchingEvents.size() >= 4) {  // ✅ 4번째 이벤트가 있는지 확인
		                ProductEventLog fourthEvent = matchingEvents.get(4);  // ✅ 4번째 이벤트 선택 (인덱스 3)
		                if (isAnomaly) {
		                    saveAnomalyLog(fourthEvent, "AI 기반 이상 탐지", "LSTM 모델 이상 패턴 감지");
		                    return;
		                }
		            }
		        }
		    }
		}

		for (ProductEventLog e : events) {
			e.setIsAnomaly(false);
			productEventLogRepo.save(e);
		}
		log.info("[정상 이벤트 저장 완료] EPC={} ", epcCode);
	}

	private boolean isDomesticProduct(String epcCode) {
		return epcCode != null && epcCode.startsWith(DOMESTIC_PREFIX);
	}

	private Optional<ProductEventLog> findDomesticFirstEventIssue(String epcCode, List<ProductEventLog> events) {
		if (!isDomesticProduct(epcCode) || events.size() < 2)
			return Optional.empty();
		ProductEventLog secondEvt = events.get(1);
		if (!List.of("aggregation", "WMS_inbound").contains(secondEvt.getEvent().getEventType())) {
			return Optional.of(secondEvt);
		}
		return Optional.empty();
	}

	private Optional<ProductEventLog> findImportedFirstEventIssue(String epcCode, List<ProductEventLog> events) {
		if (isDomesticProduct(epcCode) || events.size() < 2)
			return Optional.empty();
		ProductEventLog secondEvt = events.get(1);
		if (!"custom_outbound".equals(secondEvt.getEvent().getEventType())) {
			return Optional.of(secondEvt);
		}
		return Optional.empty();
	}

	private Optional<ProductEventLog> findDirectSellEvent(List<ProductEventLog> events) {
		boolean hasHubInbound = events.stream().anyMatch(e -> "stock_inbound(HUB)".equals(e.getEvent().getEventType()));
		if (!hasHubInbound) {
			return events.stream().filter(e -> "stock_outbound(Sell)".equals(e.getEvent().getEventType())).findFirst();
		}
		return Optional.empty();
	}

	private Optional<ProductEventLog> findUnauthorizedEvent(List<ProductEventLog> events) {
		List<String> unauthorized = List.of("illegal_transfer", "fake_aggregation", "unauthorized_custom");
		return events.stream().filter(e -> unauthorized.contains(e.getEvent().getEventType())).findFirst();
	}

	private Optional<ProductEventLog> findSequenceErrorEvent(String epcCode, List<ProductEventLog> events) {
		boolean isDomestic = isDomesticProduct(epcCode);
		List<String> flow = isDomestic ? getDomesticEventFlow() : getImportedEventFlow();

		int idx = 0;
		for (ProductEventLog evt : events) {
			String et = evt.getEvent().getEventType();
			if (idx < flow.size() && et.equals(flow.get(idx))) {
				idx++;
			} else if (idx < flow.size()) {
				return Optional.of(evt);
			} else {
				return Optional.of(evt);
			}
		}
		return Optional.empty();
	}

	private List<String> getDomesticEventFlow() {
		return List.of("commissioning", "aggregation", "WMS_inbound", "WMS_outbound", "stock_inbound(HUB)",
				"stock_outbound(HUB)", "stock_inbound(Wholesaler)", "stock_outbound(Wholesaler)",
				"stock_inbound(Reseller)", "stock_outbound(Sell)");
	}

	private List<String> getImportedEventFlow() {
		return List.of("custom_inbound", "custom_outbound", "stock_inbound(HUB)", "stock_outbound(HUB)",
				"stock_inbound(Wholesaler)", "stock_outbound(Wholesaler)", "stock_inbound(Reseller)",
				"stock_outbound(Sell)");
	}

	private void saveAnomalyLog(ProductEventLog eventLog, String anomalyType, String reason) {
		if (eventLog == null || eventLog.getProduct() == null) {
			log.warn("[이상 탐지 저장 실패] eventLog 정보 부족");
			return;
		}
		String epcCode = eventLog.getProduct().getEpcCode();

		boolean exists = anomalyLogRepository.existsByEpcCodeAndEventTime(epcCode, eventLog.getEventTime());
		if (exists) {
			log.warn("🚨 [중복 이상 탐지] 이미 저장된 anomaly_log (EPC={}, Timestamp={})", epcCode, eventLog.getEventTime());
			return;
		}

		log.warn("[이상 탐지 발생] EPC={} | 유형={} | 이유={}", epcCode, anomalyType, reason);

		eventLog.setIsAnomaly(true);
		productEventLogRepo.save(eventLog);

		AnomalyLog anomalyLog = AnomalyLog.builder()
				.anomalyType(anomalyType)
				.reason(reason)
				.productEventLog(eventLog)
				.build();
		anomalyLogRepository.save(anomalyLog);

		AnomalyDTO alert = AnomalyDTO.builder().anomalyId(anomalyLog.getAnomalyId())
				.anomalyType(anomalyLog.getAnomalyType())
				.reason(anomalyLog.getReason())
				.eventType(eventLog.getEvent()
				.getEventType()).epcCode(eventLog.getProduct().getEpcCode())
				.productName(eventLog.getProduct().getProductName())
				.hubType(eventLog.getHub().getHubType())
				.longitude(eventLog.getHub().getLongitude())
				.latitude(eventLog.getHub().getLatitude())
				.anomalyTimestamp(eventLog.getEventTime())
				.build();
				

		webSocketService.sendAnomalyAlert(Collections.singletonList(alert));
	}
}
