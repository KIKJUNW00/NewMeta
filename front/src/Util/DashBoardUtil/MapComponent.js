import React, { useEffect, useState } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function HubWiseBubbleMap() {
  const [hubData, setHubData] = useState(null);
  const mapRef = React.useRef(null);

  useEffect(() => {
    // ✅ API에서 허브 데이터 가져오기
    const fetchHubData = async () => {
      try {
        const response = await fetch('http://10.125.121.228:8080/scm/hub-wise-data');
        const data = await response.json();
        setHubData(data?.hubWiseData || {}); // 데이터가 없으면 빈 객체 설정
      } catch (error) {
        console.error('❌ API 요청 오류:', error);
        setHubData({}); // 오류 발생 시 빈 객체 설정
      }
    };

    fetchHubData();
  }, []);

  useEffect(() => {
    if (!hubData || Object.keys(hubData).length === 0) return; // 데이터가 없으면 실행 안 함

    // ✅ 기존에 존재하는 지도 제거 (리렌더링 방지)
    if (mapRef.current) {
      mapRef.current.remove();
      mapRef.current = null;
    }

    // ✅ 지도 초기화
    const map = L.map('map').setView([35.8, 127.5], 6.5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);
    mapRef.current = map;

    // ✅ 지역별 좌표 및 색상 매핑
    const hubDetails = {
      Seoul: { lat: 37.5665, lng: 126.9780, color: 'rgba(54, 162, 235, 0.5)', hoverColor: 'rgba(54, 162, 235, 1)' },
      Busan: { lat: 35.1796, lng: 129.0756, color: 'rgba(255, 99, 132, 0.5)', hoverColor: 'rgba(255, 99, 132, 1)' },
      Gwangju: { lat: 35.1595, lng: 126.8526, color: 'rgba(255, 206, 86, 0.5)', hoverColor: 'rgba(255, 206, 86, 1)' },
      Yeongju: { lat: 36.8057, lng: 128.6241, color: 'rgba(153, 102, 255, 0.5)', hoverColor: 'rgba(153, 102, 255, 1)' },
      Daejeon: { lat: 36.3504, lng: 127.3845, color: 'rgba(255, 159, 64, 0.5)', hoverColor: 'rgba(255, 159, 64, 1)' },
      Daegu: { lat: 35.8714, lng: 128.6014, color: 'rgba(75, 192, 192, 0.5)', hoverColor: 'rgba(75, 192, 192, 1)' }
    };

    // ✅ 애니메이션 효과 추가 함수
    const animateCircle = (circle, targetRadius, duration) => {
      let currentRadius = 0;
      const step = targetRadius / (duration / 16);
      const interval = setInterval(() => {
        currentRadius += step;
        if (currentRadius >= targetRadius) {
          clearInterval(interval);
          circle.setRadius(targetRadius);
        } else {
          circle.setRadius(currentRadius);
        }
      }, 16);
    };

    // ✅ 허브별 버블 표시
    Object.entries(hubData).forEach(([hub, details]) => {
      if (!hubDetails[hub]) return; // 정의되지 않은 허브는 무시

      // 각 허브의 모든 제품 수량을 합산
      const totalProducts = Object.values(details).reduce((sum, val) => sum + val, 0);
      console.log(`📌 허브명: ${hub}, 총 제품 수량: ${totalProducts}`);

      const { lat, lng, color, hoverColor } = hubDetails[hub];
      const scaleFactor = 1000; // 크기 조정 상수를 낮춤
      const bubbleSize = Math.max(3000, Math.min(Math.sqrt(totalProducts) * scaleFactor, 60000));

      // ✅ 원형 버블 추가
      const bubble = L.circle([lat, lng], {
        radius: 0, // 초기 크기 0
        color: color,
        fillColor: color,
        fillOpacity: 0.5,
        weight: 1,
      }).addTo(map);

      // ✅ 애니메이션 효과 적용
      animateCircle(bubble, bubbleSize, 700);

      // ✅ 마우스 이벤트 추가 (호버 효과)
      bubble.on('mouseover', function () {
        this.setStyle({ color: hoverColor, fillColor: hoverColor, fillOpacity: 0.5 });
        this.bindPopup(`<strong>${hub}</strong><br>총 제품 수량: ${totalProducts}`).openPopup();
      });

      bubble.on('mouseout', function () {
        this.setStyle({ color: color, fillColor: color, fillOpacity: 0.5 });
        this.closePopup();
      });
    });

    return () => {
      mapRef.current.remove(); // 언마운트 시 지도 제거
      mapRef.current = null;
    };
  }, [hubData]);

  return <div id="map" style={{ height: '500px', width: '100%' }} />;
}
