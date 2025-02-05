import React, { useState, useEffect } from 'react';
import { ResponsiveContainer, ComposedChart, Area, Line, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import axios from 'axios';

/**
 * 📌 `Chart1` - 이상 탐지 차트 (일별 / 월별)
 */
const Chart1 = () => {
  const [dailyData, setDailyData] = useState([]); // ✅ 일별 데이터
  const [monthlyData, setMonthlyData] = useState([]); // ✅ 월별 데이터

  const API_URL = 'http://10.125.121.228:8080/producteventLog'; // ✅ API 엔드포인트

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(API_URL);
        const data = response.data;

        // ✅ `isAnomaly: true` 인 데이터만 필터링
        const anomalyData = data.filter(event => event.isAnomaly);

        // ✅ 최신 7일 기준 데이터 가공
        setDailyData(generateCompleteDailyData(anomalyData));

        // ✅ 최신 5개월 기준 데이터 가공
        setMonthlyData(generateCompleteMonthlyData(anomalyData));
      } catch (error) {
        console.error('❌ 데이터 로드 실패:', error);
      }
    };

    fetchData();
  }, []);

  /**
   * 🚀 [일별 데이터 가공]
   * ✅ 최근 7일 기준
   * ✅ 날짜별 이상 탐지 개수 집계 (없으면 `0`)
   */
  const generateCompleteDailyData = (data) => {
    const dailyCounts = {};

    data.forEach(event => {
      const dateKey = event.eventTime.split(' ')[0]; // ✅ YYYY-MM-DD 형식
      dailyCounts[dateKey] = (dailyCounts[dateKey] || 0) + 1;
    });

    // ✅ 최근 7일 날짜 생성
    const days = Array.from({ length: 7 }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (6 - i)); // 7일 전부터 오늘까지
      return date.toISOString().split('T')[0]; // YYYY-MM-DD 형식
    });

    // ✅ 데이터가 없는 날짜는 `0`
    return days.map(date => ({
      name: date,
      일별: dailyCounts[date] || 0
    }));
  };

  /**
   * 🚀 [월별 데이터 가공]
   * ✅ 최근 5개월 기준
   * ✅ 월별 이상 탐지 개수 집계 (없으면 `0`)
   */
  const generateCompleteMonthlyData = (data) => {
    const monthlyCounts = {};

    data.forEach(event => {
      const [year, month] = event.eventTime.split(' ')[0].split('-'); // ✅ YYYY-MM-DD
      const monthKey = `${year}-${month}`;
      monthlyCounts[monthKey] = (monthlyCounts[monthKey] || 0) + 1;
    });

    // ✅ 최근 5개월 날짜 생성
    const months = Array.from({ length: 5 }, (_, i) => {
      const date = new Date();
      date.setMonth(date.getMonth() - (4 - i)); // 5개월 전부터 이번 달까지
      return date.toISOString().slice(0, 7); // YYYY-MM 형식
    });

    // ✅ 데이터가 없는 월은 `0`
    return months.map(month => ({
      name: month,
      월별: monthlyCounts[month] || 0
    }));
  };

  return (
    <div className="flex justify-around h-[93%] pt-[10px]">
      {/* ✅ 일별 차트 */}
      <ResponsiveContainer width="49%" height="100%">
        <ComposedChart className="border-solid border border-black" data={dailyData}
          margin={{ top: 30, right: 25, left: -25, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Area type="monotone" dataKey="일별" stroke="#8884d8" fill="#8884d8" fillOpacity={0.3} />
          <Line type="monotone" dataKey="일별" stroke="#8a2be2" strokeWidth={3} />
        </ComposedChart>
      </ResponsiveContainer>

      {/* ✅ 월별 차트 */}
      <ResponsiveContainer width="49%" height="100%">
        <ComposedChart className="border-solid border border-black" data={monthlyData}
          margin={{ top: 30, right: 25, left: -25, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip wrapperStyle={{ display: 'none' }} />
          <Area type="monotone" dataKey="월별" stroke="#8884d8" fill="#8884d8" fillOpacity={0.3} />
          <Line type="monotone" dataKey="월별" stroke="#8a2be2" strokeWidth={3} />
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  );
};

export default Chart1;
