package com.newmeta.config.emul;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType; // 올바른 MediaType 임포트
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

import com.newmeta.domain.dto.ProductEventLogDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class EpcSchedulde {

	List<ProductEventLogDTO> csv = new ArrayList<>();
	// 날짜 포맷
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// CSV 파일 경로 (예: 설정 파일에서 주입 가능)
	private static final String FILE_PATH = "C:\\Users\\user\\Desktop\\더미/이상치데이터.csv";

	// 현재 읽는 라인
	private int currentLine = 0;
	
	private Date parseDate(String dateStr) {
        try {
            if (dateStr.isEmpty()) return null;
            // 시:분만 있을 때 초:00 추가
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}")) {
                dateStr += ":00";
            }
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.warn("❌ 날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

	public EpcSchedulde() {

		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
			// 첫 줄(헤더) 건너뛰기
			reader.readLine();

			String line;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				try {
					String[] data = line.split(",");
					String epcCode = data[0].trim();
					Long productSerial = Long.parseLong(data[1].trim());
					String productName = data[2].trim();
					String hubType = data[3].trim();
					String eventType = data[4].trim();
					Date eventTime = parseDate(data[5].trim());
					Double latitude = Double.parseDouble(data[6].trim());
					Double longitude = Double.parseDouble(data[7].trim());

					if (epcCode.isEmpty()) {
						log.warn("❌ EPC 코드 누락: {}", line);
						continue;
					}
					ProductEventLogDTO dto = ProductEventLogDTO.builder()
					.epcCode(epcCode)
					.productSerial(productSerial)
					.productName(productName)
					.hubType(hubType)
					.eventType(eventType)
					.eventTime(eventTime)
					.latitude(latitude)
					.longitude(longitude)
					.build();
					
					csv.add(dto);
					
				} catch (Exception e) {
					log.error("❌ CSV 행 파싱 중 오류: row={}, err={}", line, e.getMessage());
				}
			}

		} catch (IOException e) {
			log.error("❌ CSV 파일 읽기 오류: {}", e.getMessage());
		}
	}
	 @Scheduled(fixedRate = 1000)
	    public void processCsvLinesBatch() {
	        // 현재 읽고 있는 라인에 대한 처리를 합니다.
	        if (currentLine < csv.size()) {
	            ProductEventLogDTO eventLog = csv.get(currentLine);
	            
	            // WebClient를 사용하여 데이터 전송
	            WebClient client = WebClient.builder()
	                    .baseUrl("http://localhost:8081")
	                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	                    .build();

	            // API에 POST 요청
	            Mono<Void> response = client.post()
	                    .uri("/report") // 실제 API 엔드포인트로 변경
	                    .bodyValue(eventLog) // DTO 객체를 JSON으로 변환하여 전송
	                    .retrieve()
	                    .bodyToMono(Void.class);

	            response.subscribe(
	                null, 
	                error -> log.error("❌ 데이터 전송 중 오류: {}", error.getMessage()), 
	                () -> log.info("✅ 데이터 전송 성공: {}", eventLog)
	            );

	            currentLine++; // 다음에 읽을 데이터 순서 처리
	        } else {
	            currentLine = 0; // 모든 라인을 처리한 후에는 처음으로 돌아갑니다.
	        }
	    }
}
