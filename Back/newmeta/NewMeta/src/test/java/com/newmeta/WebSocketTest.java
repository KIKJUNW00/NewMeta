//package com.newmeta;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.Map;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.newmeta.service.WebSocketService;
//
//@SpringBootTest
////@RunWith(SpringRunner.class)
//public class WebSocketTest {
//    
//    @Autowired
//    private WebSocketService webSocketService;
//
//    @Test
//    public void testWebSocketAnomalyAlert() {
//        Map<String, Object> anomalyData = Map.of(
//                "anomalyType", "위조",
//                "reason", "Commissioning 없이 다른 단계에서 감지됨.",
//                "epcCode", "8801234567890",
//                "anomalyTimestamp", new Date(),
//                "anomalyEventType", "commissioning",
//                "anomalyHub", "서울물류센터",
//                "anomalyProductName", "샘플 제품",
//                "latitude", 37.5665,
//                "longitude", 126.9780
//        );
//
//        webSocketService.sendAnomalyAlert(Collections.singletonList(anomalyData));
//    }
//}
