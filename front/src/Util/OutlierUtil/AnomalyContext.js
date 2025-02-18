import { createContext, useState, useEffect } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export const AnomalyContext = createContext();

export const AnomalyProvider = ({ children }) => {
  const [newAnomaly, setNewAnomaly] = useState(() => {
    return JSON.parse(localStorage.getItem("newAnomaly")) || false;
  });

  useEffect(() => {
    localStorage.setItem("newAnomaly", JSON.stringify(newAnomaly));
  }, [newAnomaly]);

  useEffect(() => {
    const socket = new SockJS("http://10.125.121.228:8080/ws-stomp");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("🌐 STOMP WebSocket 연결 성공 (전역)");
        stompClient.subscribe("/topic/anomalyAlerts", (message) => {
          console.log("📩 STOMP 메시지 수신:", message.body);
          try {
            const newEvent = JSON.parse(message.body);
            if (Array.isArray(newEvent) && newEvent.length > 0) {
              setNewAnomaly(true);
              localStorage.setItem("newAnomaly", JSON.stringify(true));
            }
          } catch (err) {
            console.error("❌ STOMP 메시지 파싱 오류:", err);
          }
        });
      },
      onStompError: (frame) => {
        console.error("⚠️ STOMP 오류 발생:", frame);
      },
    });

    stompClient.activate();

    return () => stompClient.deactivate();
  }, []);

  return (
    <AnomalyContext.Provider value={{ newAnomaly, setNewAnomaly }}>
      {children}
    </AnomalyContext.Provider>
  );
};
