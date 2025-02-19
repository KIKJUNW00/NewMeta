import React, { useEffect, useState } from 'react';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

export default function HubWiseBubbleMap() {
  const [hubData, setHubData] = useState(null);
  const mapRef = React.useRef(null);

  useEffect(() => {
    const fetchHubData = async () => {
      try {
        const response = await fetch('http://localhost:8080/scm/hub-wise-data');
        const data = await response.json();
        setHubData(data?.hubWiseData || {});
      } catch (error) {
        console.error('❌ API 요청 오류:', error);
        setHubData({});
      }
    };
    fetchHubData();
  }, []);

  useEffect(() => {
    if (!hubData || Object.keys(hubData).length === 0) return;

    if (mapRef.current) {
      mapRef.current.remove();
      mapRef.current = null;
    }

    const map = L.map('map').setView([35.8, 127.5], 6.5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);
    mapRef.current = map;

    const hubDetails = {
      Busan_dongnea: { lat: 35.1946, lng: 129.0615, color: 'rgba(54, 162, 235, 0.5)', hoverColor: 'rgba(54, 162, 235, 1)' },
      Busan_gumjung: { lat: 35.2392, lng: 129.0895, color: 'rgba(255, 99, 132, 0.5)', hoverColor: 'rgba(255, 99, 132, 1)' },
      Busan_yangsan: { lat: 35.3389, lng: 129.034, color: 'rgba(255, 206, 86, 0.5)', hoverColor: 'rgba(255, 206, 86, 1)' },

      Daegu_bukgu: { lat: 35.9444, lng: 128.5644, color: 'rgba(153, 102, 255, 0.5)', hoverColor: 'rgba(153, 102, 255, 1)' },
      Daegu_donggu: { lat: 35.8857, lng: 128.639, color: 'rgba(255, 159, 64, 0.5)', hoverColor: 'rgba(255, 159, 64, 1)' },
      Daegu_junggu: { lat: 35.8714, lng: 128.6014, color: 'rgba(75, 192, 192, 0.5)', hoverColor: 'rgba(75, 192, 192, 1)' },

      Daejeon_bukgu: { lat: 36.3731, lng: 127.3947, color: 'rgba(199, 199, 199, 0.5)', hoverColor: 'rgba(199, 199, 199, 1)' },
      Daejeon_donggu: { lat: 36.3159, lng: 127.4546, color: 'rgba(255, 205, 210, 0.5)', hoverColor: 'rgba(255, 205, 210, 1)' },
      Daejeon_junggu: { lat: 36.3214, lng: 127.4191, color: 'rgba(171, 71, 188, 0.5)', hoverColor: 'rgba(171, 71, 188, 1)' },

      Gwangju_bukgu: { lat: 35.1796, lng: 126.9156, color: 'rgba(244, 67, 54, 0.5)', hoverColor: 'rgba(244, 67, 54, 1)' },
      Gwangju_donggu: { lat: 35.1484, lng: 126.9228, color: 'rgba(103, 58, 183, 0.5)', hoverColor: 'rgba(103, 58, 183, 1)' },
      Gwangju_junggu: { lat: 35.1595, lng: 126.8514, color: 'rgba(63, 81, 181, 0.5)', hoverColor: 'rgba(63, 81, 181, 1)' },

      Kangwon: { lat: 37.8228, lng: 128.1555, color: 'rgba(33, 150, 243, 0.5)', hoverColor: 'rgba(33, 150, 243, 1)' },

      Seoul_bukgu: { lat: 37.6121, lng: 127.0294, color: 'rgba(156, 204, 101, 0.5)', hoverColor: 'rgba(156, 204, 101, 1)' },
      Seoul_donggu: { lat: 37.5733, lng: 127.0246, color: 'rgba(255, 238, 88, 0.5)', hoverColor: 'rgba(255, 238, 88, 1)' },
      Seoul_junggu: { lat: 37.5642, lng: 126.9978, color: 'rgba(76, 175, 80, 0.5)', hoverColor: 'rgba(76, 175, 80, 1)' },

      Yeongju: { lat: 36.8057, lng: 128.6241, color: 'rgba(121, 85, 72, 0.5)', hoverColor: 'rgba(121, 85, 72, 1)' },
    };

    const bubbles = [];

    Object.entries(hubData).forEach(([hub, totalProducts]) => {
      if (hubDetails[hub]) {
        const { lat, lng, color, hoverColor } = hubDetails[hub];
        const baseSize = Math.sqrt(totalProducts) * 12000;
        const bubbleSize = Math.max(3000, Math.min(baseSize, 30000));

        const bubble = L.circle([lat, lng], {
          radius: bubbleSize,
          color: color,
          fillColor: color,
          fillOpacity: 0.5,
          weight: 1,
        }).addTo(map);

        bubble.on('mouseover', function () {
          this.setStyle({ color: hoverColor, fillColor: hoverColor, fillOpacity: 0.7 });
          this.bindPopup(`<strong>${hub}</strong><br>총 제품 수량: ${totalProducts}`).openPopup();
        });

        bubble.on('mouseout', function () {
          this.setStyle({ color: color, fillColor: color, fillOpacity: 0.5 });
          this.closePopup();
        });

        bubbles.push({ bubble, baseSize });
      }
    });

    // 지도 확대/축소 시 버블 크기 조정
    map.on('zoomend', () => {
      const zoomLevel = map.getZoom();
      bubbles.forEach(({ bubble, baseSize }) => {
        const adjustedSize = baseSize * (1 / (zoomLevel * 0.5)); // 축소 시 커지고, 확대 시 작아짐
        const newRadius = Math.max(4000, Math.min(adjustedSize, 60000)); // 최소/최대 크기 제한
        bubble.setRadius(newRadius);
      });
    });

    return () => {
      mapRef.current.remove();
      mapRef.current = null;
    };
  }, [hubData]);

  return <div id="map" style={{ height: '500px', width: '100%' }} />;
}