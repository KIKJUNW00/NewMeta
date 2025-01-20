import React, { useEffect } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

// Leaflet 마커 아이콘 경로 설정
delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

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

  
  return (
    <div
      id="map"
      className="h-full w-full"
      style={{ height: '835px', width: '100%' }}
    >
      
    </div>


    
  );
}
