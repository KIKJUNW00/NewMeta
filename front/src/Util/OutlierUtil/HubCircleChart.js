import React, { useEffect, useState } from 'react';
import axios from 'axios';

const hubNameMap = {
  Daegu_bukgu: "대구 북구",
  Daejeon_bukgu: "대전 북구",
  Daegu_junggu: "대구 중구",
  Busan_dongnea: "부산 동구",
  Seoul_donggu: "서울 동구",
  Gwangju_bukgu: "광주 북구",
  Busan_gumjung: "부산 금정",
  Daejeon_donggu: "대전 동구",
  Gwangju_donggu: "광주 동구",
  Yeongju: "영주",
  Seoul_bukgu: "서울 북구",
  kangwon: "강원",
  Seoul_junggu: "서울 중구",
  Daejeon_junggu: "대전 중구",
  Busan_yangsan: "양산",
  Daegu_donggu: "대구 동구",
  Gwangju_junggu: "광주 중구",
  Kangwon: "강원",
};

const hubs = [
  "Daegu_bukgu", "Daejeon_bukgu", "Daegu_junggu", "Busan_dongnea", "Seoul_donggu",
  "Gwangju_bukgu", "Busan_gumjung", "Daejeon_donggu", "Gwangju_donggu", "Yeongju",
  "Seoul_bukgu", "kangwon", "Seoul_junggu", "Daejeon_junggu", "Busan_yangsan",
  "Daegu_donggu", "Gwangju_junggu", "Kangwon"
];

const HubCircleChart = () => {
  const [hubCounts, setHubCounts] = useState({});
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6; // 한 페이지에 표시할 허브 개수
  const API_URL = 'http://localhost:8080/anomalies/paged';

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(API_URL);
        const data = response.data.content;

        const hubTypeCounts = {};
        data.forEach((item) => {
          const hub = item.anomalyHub.trim();
          hubTypeCounts[hub] = (hubTypeCounts[hub] || 0) + 1;
        });

        const fullHubCounts = hubs.reduce((acc, hub) => {
          acc[hub] = hubTypeCounts[hub] || 0;
          return acc;
        }, {});

        setHubCounts(fullHubCounts);
      } catch (error) {
        console.error('❌ 데이터 로드 실패:', error);
      }
    };

    fetchData();
  }, []);

  // 현재 페이지에 표시할 데이터 계산
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentHubs = hubs.slice(startIndex, startIndex + itemsPerPage);

  const totalPages = Math.ceil(hubs.length / itemsPerPage);

  const handlePageChange = (newPage) => {
    if (newPage > 0 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  return (
    <div className="w-full">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {currentHubs.map((hub, index) => (
          <div key={index} className="flex flex-col items-center bg-white p-4 rounded shadow">
            <div className="relative w-14 h-14">
              <svg className="w-14 h-14">
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
                  strokeDasharray="226"
                  strokeDashoffset={226 - (226 * (hubCounts[hub] || 0)) / 10}
                />
              </svg>
              <div className="absolute inset-0 flex items-center justify-center">
                <span className="text-sm font-bold text-gray-800">{hubCounts[hub] || 0}</span>
              </div>
            </div>
            <p className="text-center mt-2 text-xs font-medium text-gray-600">{hubNameMap[hub]}</p>
          </div>
        ))}
      </div>

      {/* 페이지 네비게이션 */}
      <div className="flex justify-center mt-16 space-x-2">
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50 rounded"
        >
          이전
        </button>
        <span className="px-4 py-2 text-gray-700">
          {currentPage} / {totalPages}
        </span>
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50 rounded"
        >
          다음
        </button>
      </div>
    </div>

  );
};

export default HubCircleChart;
