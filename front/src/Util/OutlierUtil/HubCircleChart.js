import React, { useEffect, useState } from 'react';
import axios from 'axios';

const hubNameMap = {
  Gwangju: "광주",
  Yeongju: "영주",
  Seoul: "서울",
  Busan: "부산",
  Daejeon: "대전",
  Daegu: "대구"
};

const hubs = ["Seoul", "Daejeon", "Yeongju", "Gwangju", "Daegu", "Busan"]; // ✅ 허브 목록 (영문으로 작성)

const HubCircleChart = () => {
  const [hubCounts, setHubCounts] = useState({});

  const API_URL = 'http://10.125.121.228:8080/anomalies';

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(API_URL);
        const data = response.data.content;
        console.log("📊 데이터 확인:", data); // ✅ 전체 데이터 확인
  
        const hubTypeCounts = {};
  
        data.forEach((item) => {
          console.log("📍 anomalyHub 값:", item.anomalyHub); // ✅ anomalyHub 값 확인
          const hub = item.anomalyHub.trim(); // 공백 제거
          hubTypeCounts[hub] = (hubTypeCounts[hub] || 0) + 1;
        });
  
        const fullHubCounts = hubs.reduce((acc, hub) => {
          acc[hub] = hubTypeCounts[hub] || 0;
          return acc;
        }, {});
  
        console.log("✅ 최종 집계된 허브 카운트:", fullHubCounts); // ✅ 최종 값 확인
        setHubCounts(fullHubCounts);
      } catch (error) {
        console.error('❌ 데이터 로드 실패:', error);
      }
    };
  
    fetchData();
  }, []);
  

  return (
    <div className="w-full">
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {hubs.map((hub, index) => (
          <div key={index} className="flex flex-col items-center">
            <div className="relative w-16 h-16">
              <svg className="w-16 h-16">
                <circle
                  className="text-gray-300"
                  strokeWidth="6"
                  stroke="currentColor"
                  fill="transparent"
                  r="24"
                  cx="50%"
                  cy="50%"
                />
                <circle
                  className="text-green-500 transition-all duration-700"
                  strokeWidth="6"
                  strokeLinecap="round"
                  stroke="currentColor"
                  fill="transparent"
                  r="24"
                  cx="50%"
                  cy="50%"
                  strokeDasharray="150"
                  strokeDashoffset={150 - (150 * (hubCounts[hub] || 0)) / 10} // 비율 애니메이션
                />
              </svg>
              <div className="absolute inset-0 flex items-center justify-center">
                <span className="text-sm font-bold text-gray-700">
                  {hubCounts[hub] || 0}
                </span>
              </div>
            </div>
            {/* ✅ hubNameMap을 사용해 한글 허브 이름으로 표시 */}
            <p className="text-center mt-1 text-xs font-medium text-gray-600">{hubNameMap[hub]}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default HubCircleChart;
