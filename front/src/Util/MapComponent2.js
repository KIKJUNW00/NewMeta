import React, { useEffect, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function MapComponent2({ readPoints }) {
  const mapRef = useRef(null); // 지도 객체 저장
  const mapContainerRef = useRef(null); // DOM 컨테이너 참조
  const markerLayerRef = useRef(null); // 마커 레이어 참조

  useEffect(() => {
    // 지도 초기화
    if (!mapRef.current) {
      const map = L.map(mapContainerRef.current, {
        center: [36.5665, 127.9780], // 초기 중심 좌표
        zoom: 6,
      });

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
      }).addTo(map);

      mapRef.current = map;
    }
  }, []);

  useEffect(() => {
    // 기존 마커와 선 제거
    if (markerLayerRef.current) {
      markerLayerRef.current.clearLayers();
    }

    // 새로운 마커와 선 추가
    if (readPoints && readPoints.length > 0) {
      const markerLayer = L.layerGroup();
      const polyline = L.polyline(readPoints, { color: 'blue', weight: 2 }).addTo(markerLayer);

      readPoints.forEach(([lat, lng]) => {
        L.circleMarker([lat, lng], {
          radius: 5,
          color: 'black',
          fillColor: 'yellowgreen',
          fillOpacity: 1,
        }).addTo(markerLayer);
      });

      markerLayer.addTo(mapRef.current);
      markerLayerRef.current = markerLayer;
    }
  }, [readPoints]); // readPoints 변경 시 업데이트

  return (
    <div
      ref={mapContainerRef}
      style={{
        height: '350px',
        width: '100%',
      }}
    />
  );
}
