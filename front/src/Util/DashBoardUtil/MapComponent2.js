import React, { useEffect, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function MapComponent2({ readPoints, selectedEpcCode }) {
  const mapRef = useRef(null);
  const mapContainerRef = useRef(null);
  const markerLayerRef = useRef(null);

  useEffect(() => {
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
    if (!mapRef.current || !Array.isArray(readPoints) || readPoints.length === 0) return;

    // 기존 마커 및 선 제거
    if (markerLayerRef.current) {
        markerLayerRef.current.clearLayers();
    } else {
        markerLayerRef.current = L.layerGroup().addTo(mapRef.current);
    }

    // 선택된 제품의 EPC Code에 해당하는 데이터만 필터링 후 시간순 정렬
    let filteredPoints = readPoints
      .filter(point => point.epcCode === selectedEpcCode && point.latitude !== undefined && point.longitude !== undefined)
      .sort((a, b) => new Date(a.eventTime) - new Date(b.eventTime)); // eventTime 기준 정렬

    if (filteredPoints.length === 0) {
        console.warn("유효한 좌표가 없습니다.");
        return;
    }

    //  중복된 위도, 경도를 제거 (같은 좌표는 1개만 유지)
    const uniquePoints = [];
    const seenCoordinates = new Set();

    filteredPoints.forEach((point) => {
        const coordKey = `${point.latitude}-${point.longitude}`;
        if (!seenCoordinates.has(coordKey)) {
            seenCoordinates.add(coordKey);
            uniquePoints.push(point);
        }
    });

    // 이동 경로 추가 (파란색 선)
    const validPoints = uniquePoints.map(point => [point.latitude, point.longitude]);
    L.polyline(validPoints, { color: "black", weight: 3 }).addTo(markerLayerRef.current);

    // 각 위치에 마커 및 순번 라벨 추가
    uniquePoints.forEach((point, index) => {
      const { latitude, longitude, anomaly } = point;
      
      // anomaly 상태에 따라 색상 설정 (정상: 초록색, 이상: 빨간색)
      const fillColor = anomaly ? "red" : "yellowgreen";

      //  번호 아이콘 생성 (번호가 잘 보이도록 스타일 개선)
      const numberIcon = L.divIcon({
        className: "custom-number-marker",
        html: `<div style="
          width: 28px; /* 크기 키움 */
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px; /* 숫자 크기 키움 */
          font-weight: bold;
          color: white; /* 글자색 흰색 */
          text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.6); /* 가독성 개선 */
        ">${index + 1}</div>`, // ✅ 1부터 시작하도록 수정
        iconSize: [28, 28],
      });

      // 🎯 번호 마커 추가 (숫자 라벨)
      L.marker([latitude, longitude], { icon: numberIcon }).addTo(markerLayerRef.current);

      // 🎯 위치 점 마커 추가 (색상: 정상(초록), 이상(빨강))
      L.circleMarker([latitude, longitude], {
        radius: 15, // 조금 더 키움
        fillColor: fillColor,
        fillOpacity: 1,
      }).addTo(markerLayerRef.current);
    });

    // 지도 중심 이동 X (사용자가 직접 이동할 수 있도록 유지)
  }, [readPoints, selectedEpcCode]);

  return <div ref={mapContainerRef} style={{ height: '350px', width: '100%' }} />;
}
