import React, { useState, useEffect } from 'react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import axios from 'axios';

const Chart1 = () => {
  const [dailyData, setDailyData] = useState([]);
  const [monthlyData, setMonthlyData] = useState([]);

  const API_URL = 'http://10.125.121.228:8080/producteventLog';

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(API_URL);
        const data = response.data;
        const anomalyData = data.filter(event => event.isAnomaly);

        setDailyData(generateCompleteDailyData(anomalyData));
        setMonthlyData(generateCompleteMonthlyData(anomalyData));
      } catch (error) {
        console.error('❌ 데이터 로드 실패:', error);
      }
    };

    fetchData();
  }, []);

  const generateCompleteDailyData = (data) => {
    const dailyCounts = {};
    data.forEach(event => {
      const dateKey = event.eventTime.split(' ')[0];
      dailyCounts[dateKey] = (dailyCounts[dateKey] || 0) + 1;
    });

    const days = Array.from({ length: 7 }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (6 - i));
      return date.toISOString().split('T')[0];
    });

    return days.map(date => ({
      name: date,
      count: dailyCounts[date] || 0
    }));
  };

  const generateCompleteMonthlyData = (data) => {
    const monthlyCounts = {};
    data.forEach(event => {
      const [year, month] = event.eventTime.split(' ')[0].split('-');
      const monthKey = `${year}-${month}`;
      monthlyCounts[monthKey] = (monthlyCounts[monthKey] || 0) + 1;
    });

    const months = Array.from({ length: 5 }, (_, i) => {
      const date = new Date();
      date.setMonth(date.getMonth() - (4 - i));
      return date.toISOString().slice(0, 7);
    });

    return months.map(month => ({
      name: month,
      count: monthlyCounts[month] || 0
    }));
  };

  return (
    <div>
      
      {/* ✅ 일별 차트 */}
      <div className="mb-2">
        <h5 className="text-base text-left">일별 이상 탐지</h5>
        <ResponsiveContainer width="100%" height={200}>
          <AreaChart
            data={dailyData}
            margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Area type="monotone" dataKey="count" stroke="#8884d8" fill="#8884d8" />
          </AreaChart>
        </ResponsiveContainer>
      </div>

      {/* ✅ 월별 차트 */}
      <div className="mb-8">
        <h5 className="text-base text-left">월별 이상 탐지</h5>
        <ResponsiveContainer width="100%" height={200}>
          <AreaChart
            data={monthlyData}
            margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Area type="monotone" dataKey="count" stroke="#82ca9d" fill="#82ca9d" />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default Chart1;
