package com.newmeta.service; // 서비스 클래스가 속한 패키지를 선언

import com.fasterxml.jackson.databind.ObjectMapper; // JSON 직렬화 및 역직렬화를 위한 ObjectMapper 임포트
import com.newmeta.domain.dto.AnomalyDTO;
import com.newmeta.domain.dto.ProductEventLogDTO;

import lombok.RequiredArgsConstructor; // Lombok의 필수 생성자 자동 생성 어노테이션
import lombok.extern.slf4j.Slf4j; // 로깅을 위한 Lombok 어노테이션
import org.springframework.messaging.simp.SimpMessagingTemplate; // Spring WebSocket 메시지 전송을 위한 클래스
import org.springframework.stereotype.Service; // Spring의 Service 어노테이션
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 📡 WebSocket을 통한 실시간 데이터 제공 서비스
 * ✅ 기존 SCMWebSocketHandler의 기능을 WebSocketService로 통합
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송을 위한 SimpMessagingTemplate
    private final ObjectMapper objectMapper; // JSON 변환 객체 (Java 객체를 JSON으로 변환 및 역변환)
    
    // ✅ WebSocket 세션 관리 (여러 스레드에서 안전하게 관리 가능)
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    /**
     * ✅ WebSocket 세션 추가
     * @param session 클라이언트 WebSocket 세션
     */
    public void addSession(WebSocketSession session) {
        sessions.add(session);
        log.info("📡 WebSocket 연결됨: {}", session.getId());
    }

    /**
     * ✅ WebSocket 세션 제거
     * @param session 클라이언트 WebSocket 세션
     */
    public void removeSession(WebSocketSession session) {
    	sessions.removeIf(s -> !s.isOpen()); // ✅ 닫힌 세션 즉시 제거
        log.info("❌ WebSocket 연결 종료: {}", session.getId());
    }


    /**
     * 📡 WebSocket을 통해 실시간 데이터 전송
     */
    public void sendRealTimeData(List<Map<String, Object>> data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonData));
                    log.info("📡 WebSocket 메시지 전송 완료: 세션 ID={}", session.getId());
                }
            }
        } catch (IOException e) {
            log.error("⚠️ WebSocket 데이터 전송 오류", e);
        }
    }

    /**
     * 🚨 [이상 탐지 알림 전송]
     */
    public void sendAnomalyAlert(List<AnomalyDTO> anomalyDataList) {
        try {
            messagingTemplate.convertAndSend("/topic/anomalyAlerts", anomalyDataList);
            log.info("📡 WebSocket - 이상 탐지 알림 전송 완료: {}", anomalyDataList);
        } catch (Exception e) {
            log.error("❌ WebSocket 전송 오류", e);
        }
    }
    



    /**
     * 🚀 [SCM 실시간 데이터 전송]
     */
    public void sendRealTimeSCMData(Map<String, Object> scmData) {
        try {
            messagingTemplate.convertAndSend("/topic/scmDataUpdate", scmData);
            log.info("📡 WebSocket - SCM 데이터 전송 완료: {}", scmData);
        } catch (Exception e) {
            log.error("❌ WebSocket 전송 오류", e);
        }
    }
    

    /**
     * 🚀 [허브별 실시간 물류 데이터 WebSocket 전송]
     */
    public void sendHubWiseData(Map<String, Object> hubWiseData) {
        try {
            messagingTemplate.convertAndSend("/topic/hubWiseData", Map.of("hubWiseData", hubWiseData));
            log.info("📡 WebSocket - 허브별 집계 데이터 전송 완료");
        } catch (Exception e) {
            log.error("❌ WebSocket 전송 오류", e);
        }
    }
}
