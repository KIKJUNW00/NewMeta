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

@Configuration
@Slf4j
public class EpcSchedulde {

	List<ProductEventLogDTO> csv = new ArrayList<>();
	// 날짜 포맷
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// CSV 파일 경로 (예: 설정 파일에서 주입 가능)
	private static final String FILE_PATH = "C:\\Users\\user\\Desktop\\더미/실험데이터(120,50).csv";

	// 현재 읽는 라인
	private int currentLine = 0;
	
	private boolean allProcessed = false;  // 🚩 모든 데이터가 처리되었는지 확인하는 플래그
	
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
	@Scheduled(fixedRate = 50)
	public void processCsvLinesBatch() {
	    if (allProcessed) {
	        return;  // 모든 데이터가 처리되었으면 더 이상 실행하지 않음
	    }

	    if (currentLine < csv.size()) {
	        ProductEventLogDTO eventLog = csv.get(currentLine);
	        
	        // WebClient를 사용하여 데이터 전송
	        WebClient client = WebClient.builder()
	                .baseUrl("http://localhost:8080")
	                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	                .build();

	        // API에 POST 요청
	        client.post()
	                .uri("/report")
	                .bodyValue(eventLog)
	                .retrieve()
	                .bodyToMono(Void.class)
	                .subscribe(
	                        success -> log.info("✅ 데이터 전송 성공: {}", eventLog),
	                        error -> log.error("❌ 데이터 전송 중 오류: {}", error.getMessage())
	                );

	        currentLine++;  // 다음 라인으로 이동
	    } else {
	        log.info("🚀 모든 데이터를 처리했습니다. 스케줄링을 중지합니다.");
	        allProcessed = true;  // 모든 데이터 처리가 완료되었음을 표시
	    }
	}

}
