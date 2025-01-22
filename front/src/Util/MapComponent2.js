import React, { useEffect, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function MapComponent2() {
  const mapRef = useRef(null); // 지도 객체 저장
  const mapContainerRef = useRef(null); // DOM 컨테이너 참조

  useEffect(() => {
    // 지도 객체가 초기화되어 있지 않은 경우에만 실행
    if (!mapRef.current) {
      // 지도 초기화
      const map = L.map(mapContainerRef.current, {
        center: [30.496253846540917, 115.64034702100716], // 중심 좌표
        zoom: 3, // 줌 레벨
      });

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
      }).addTo(map);

      // 지도 객체를 ref에 저장
      mapRef.current = map;
    }

    // 컴포넌트 언마운트 시 리소스 정리
    return () => {
      if (mapRef.current) {
        mapRef.current.remove(); // Leaflet 지도 제거
        mapRef.current = null; // ref 초기화
      }
    };
  }, []);

  return (
    <div
      ref={mapContainerRef} // 지도 컨테이너에 ref 연결
      style={{
        height: '350px', // 지도 높이
        width: '100%', // 지도 너비
      }}
    />
  );
}
