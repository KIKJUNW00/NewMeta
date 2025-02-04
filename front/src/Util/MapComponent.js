import React, { useEffect, useState } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function HubWiseBubbleMap() {
  const [hubData, setHubData] = useState(null);
  const mapRef = React.useRef(null);

  useEffect(() => {
    // API에서 허브 데이터 가져오기
    const fetchHubData = async () => {
      try {
        const response = await fetch('http://10.125.121.228:8080/scm/hub-wise-data');
        const data = await response.json();
        setHubData(data.hubWiseData);
      } catch (error) {
        console.error('API 요청 오류:', error);
      }
    };

    fetchHubData();
  }, []);

  
  useEffect(() => {
    if (!hubData) return;

    // 지도 초기화 (한 번만 실행)
    if (!mapRef.current) {
      const map = L.map('map').setView([35.8, 127.5], 6.5); // 대한민국 중심 좌표
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
      }).addTo(map);
      mapRef.current = map;
    }


    // 지역별 좌표 및 색상 매핑
    const hubDetails = {
      'Seoul': {
        lat: 37.5665,
        lng: 126.9780,
        color: 'rgba(54, 162, 235, 0.5)', // 파란색
        hoverColor: 'rgba(54, 162, 235, 1)'
      },
      'Incheon': {
        lat: 37.5,
        lng: 126.7052,
        color: 'rgba(75, 192, 75, 0.5)', // 초록색
        hoverColor: 'rgba(75, 192, 75, 1)'
      },
      'Busan': {
        lat: 35.1796,
        lng: 129.0756,
        color: 'rgba(255, 99, 132, 0.5)', // 빨간색
        hoverColor: 'rgba(255, 99, 132, 1)'
      },
      'Gwangju': {
        lat: 35.1595,
        lng: 126.8526,
        color: 'rgba(255, 206, 86, 0.5)', // 노란색
        hoverColor: 'rgba(255, 206, 86, 1)'
      },
      'Yeongju': {
        lat: 36.8057,
        lng: 128.6241,
        color: 'rgba(153, 102, 255, 0.5)', // 보라색
        hoverColor: 'rgba(153, 102, 255, 1)'
      },
      'Daejeon': {
        lat: 36.3504,
        lng: 127.3845,
        color: 'rgba(255, 159, 64, 0.5)', // 주황색
        hoverColor: 'rgba(255, 159, 64, 1)'
      },
      'Daegu': {
        lat: 35.8714,
        lng: 128.6014,
        color: 'rgba(75, 192, 192, 0.5)', // 청록색
        hoverColor: 'rgba(75, 192, 192, 1)'
      }
    };

    // 애니메이션 효과 추가 함수
    const animateCircle = (circle, targetRadius, duration) => {
      let currentRadius = 0;
      const step = targetRadius / (duration / 16.0); // 16ms마다 업데이트

      const interval = setInterval(() => {
        currentRadius += step;
        if (currentRadius >= targetRadius) {
          currentRadius = targetRadius;
          clearInterval(interval);
        }
        circle.setRadius(currentRadius);
      }, 16);
    };

    // 허브별 버블 표시
    Object.entries(hubData).forEach(([hub, details]) => {
      if (!hubDetails[hub]) return; // 해당 지역이 정의되지 않으면 무시
      

      const totalProducts = Object.values(details.domestic).reduce((sum, val) => sum + val, 0);
      console.log(`📌 허브명: ${hub}, 총 제품 수량: ${totalProducts}`);
      const { lat, lng, color, hoverColor } = hubDetails[hub];

      // 🔹 면적 비율을 유지하면서 크기 조절 (최소 3000, 최대 50000)
      const scaleFactor = 7000; // 크기 조정 상수
      const bubbleSize = Math.max(3000, Math.min(Math.sqrt(totalProducts) * scaleFactor, 60000));

      // 원형 버블 추가 (초기 크기 0)
      const bubble = L.circle([lat, lng], {
        radius: 0, // 초기 크기 0
        color: color,
        fillColor: color,
        fillOpacity: 0.5,
        weight: 1,
      }).addTo(mapRef.current);

      // 애니메이션 효과로 버블 크기 확대
      animateCircle(bubble, bubbleSize, 700); // 700ms 동안 확대

      // 마우스 이벤트 추가 (호버 효과)
      bubble.on('mouseover', function () {
        this.setStyle({
          color: hoverColor,
          fillColor: hoverColor,
          fillOpacity: 0.8,
        });
        this.bindPopup(
          `<strong>${hub}</strong><br>총 제품 수량: ${totalProducts}`
        ).openPopup();
      });

      bubble.on('mouseout', function () {
        this.setStyle({
          color: color,
          fillColor: color,
          fillOpacity: 0.5,
        });
        this.closePopup();
      });
    });

    return () => {
      mapRef.current.remove(); // 컴포넌트 언마운트 시 지도 제거
      mapRef.current = null;
    };
  }, [hubData]);

  return (
    <div
      id="map"
      style={{
        height: '500px',
        width: '100%',
      }}
    />
  );
}
