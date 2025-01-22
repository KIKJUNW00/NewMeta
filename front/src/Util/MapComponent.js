import React, { useEffect , useRef} from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function AnimatedBubbleMapWithLegend() {

  // const mapRef = useRef(null); // 지도 객체를 저장하는 Ref

  useEffect(() => {

    // if (mapRef.current) {
    //   // 이미 초기화된 경우 함수 종료
    //   return;
    // }


    // 지도 초기화
    const map = L.map('map').setView([30.496253846540917, 115.64034702100716], 3); // 한국과 중국 중심 좌표

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);

    // 데이터: 좌표, 물품 양, 국가 이름
    const bubbleData = [
      {
        lat: 36.5665, // 서울 (위도)
        lng: 127.9780, // 서울 (경도)
        size: 50, // 물품 수량
        country: 'Korea', // 국가 이름
        color: 'rgba(54, 162, 235, 0.5)', // 기본 색상
        hoverColor: 'rgba(54, 162, 235, 1)', // 호버 색상
      },
      {
        lat: 35.9042, // 베이징 (위도)
        lng: 110.4074, // 베이징 (경도)
        size: 80, // 물품 수량
        country: 'China', // 국가 이름
        color: 'rgba(255, 99, 132, 0.5)', // 기본 색상
        hoverColor: 'rgba(255, 99, 132, 1)', // 호버 색상
      },
    ];

    // 애니메이션 효과를 추가하는 함수
    const animateCircle = (circle, targetRadius, duration) => {
      let currentRadius = 100;
      const step = targetRadius / (duration / 16.0); // 10ms마다 업데이트

      const interval = setInterval(() => {
        currentRadius += step;
        if (currentRadius >= targetRadius) {
          currentRadius = targetRadius;
          clearInterval(interval);
        }
        circle.setRadius(currentRadius);
      }, 10);
    };

    // 지도에 버블 추가
    bubbleData.forEach((bubble) => {
      // Circle 초기 크기는 0으로 설정
      const bubbleCircle = L.circle([bubble.lat, bubble.lng], {
        radius: 0, // 초기 크기 0
        color: bubble.color,
        fillColor: bubble.color,
        fillOpacity: 0.5,
        weight: 1,
      }).addTo(map);

      // 애니메이션으로 버블 크기 증가
      animateCircle(bubbleCircle, bubble.size * 7000, 600); // 500ms 동안 확대

      // 마우스 이벤트 추가
      bubbleCircle.on('mouseover', function () {
        this.setStyle({
          color: bubble.hoverColor,
          fillColor: bubble.hoverColor,
          fillOpacity: 0.7,
        });
        this.bindPopup(
          `<strong>${bubble.country}</strong><br>물품 양: ${bubble.size}`
        ).openPopup();
      });

      bubbleCircle.on('mouseout', function () {
        this.setStyle({
          color: bubble.color,
          fillColor: bubble.color,
          fillOpacity: 0.5,
        });
        this.closePopup();
      });
    });


    return () => {
      map.remove(); // 컴포넌트 언마운트 시 지도 제거
    };
  }, []);

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
