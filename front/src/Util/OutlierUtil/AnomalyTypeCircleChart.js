import React, { useEffect, useState } from 'react';
import axios from 'axios';

const anomalyTypes = [
  "이상 이벤트 발생",
  "이벤트 순서 오류",
  "위조",
  "밀수",
  "불법 유통",
  "EPC 코드 중복 이상치",
  "AI 기반 이상 탐지"
];

const AnomalyTypeCircleChart = () => {
  const [anomalyCounts, setAnomalyCounts] = useState({});

  const API_URL = 'http://10.125.121.228:8080/anomalies';

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(API_URL);
        const data = response.data.content;

        const typeCounts = {};

        // ✅ anomalyType별로 데이터 집계
        data.forEach((item) => {
          const type = item.anomalyType;
          typeCounts[type] = (typeCounts[type] || 0) + 1;
        });

        // ✅ 7개의 anomalyType을 기준으로 없는 값은 0으로 채움
        const fullTypeCounts = anomalyTypes.reduce((acc, type) => {
          acc[type] = typeCounts[type] || 0;
          return acc;
        }, {});

        setAnomalyCounts(fullTypeCounts);
      } catch (error) {
        console.error('❌ 데이터 로드 실패:', error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="w-full">
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {anomalyTypes.map((type, index) => (
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
                  className="text-blue-500 transition-all duration-700"
                  strokeWidth="6"
                  strokeLinecap="round"
                  stroke="currentColor"
                  fill="transparent"
                  r="24"
                  cx="50%"
                  cy="50%"
                  strokeDasharray="150" // ✅ 반지름 r=24일 때 전체 둘레 약 150
                  strokeDashoffset={150 - (150 * (anomalyCounts[type] || 0)) / 10} // 비율 애니메이션
                />
              </svg>
              <div className="absolute inset-0 flex items-center justify-center">
                <span className="text-sm font-bold text-gray-700">
                  {anomalyCounts[type] || 0}
                </span>
              </div>
            </div>
            <p className="text-center mt-1 text-xs font-medium text-gray-600">{type}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AnomalyTypeCircleChart;
