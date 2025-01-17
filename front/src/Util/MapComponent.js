import React, { useEffect } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function MapComponent() {
  useEffect(() => {
    // 지도 초기화
    const map = L.map('map').setView([37.5665, 126.9780], 13); // 서울 좌표 (위도, 경도)와 줌 레벨

    L.marker([37.5665, 126.9780]) // 서울 좌표
    .addTo(map)
    .bindPopup('여기는 서울입니다.')
    .openPopup();


    // OpenStreetMap 타일 추가
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);

    return () => {
      map.remove(); // 컴포넌트 언마운트 시 지도 제거
    };
  }, []);

  console.log("MapComponent rendered");//
  return (
    // <div
    //   id="map"
    //   className="h-full w-full"
    //   style={{ height: '100%', width: '100%' }}
    // ></div>
    
    <div id="map-container" style={{ height: "100%", width: "100%" }}>Map Here</div>//
  );
}
