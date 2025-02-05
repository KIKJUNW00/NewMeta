import React, { useState, useEffect } from 'react';

export default function Clock() {
  const [date, setDate] = useState(new Date());

  useEffect(() => {
    // 1초마다 현재 시간을 업데이트하는 타이머 설정
    const timer = setInterval(() => {
      setDate(new Date());
    }, 1000);

    // 컴포넌트 언마운트 시 타이머 정리
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="flex flex-col items-center text-white">
      {/* 날짜 */}
      <div className="text-lg font-semibold">
        {date.toLocaleDateString()} {/* 날짜만 표시 */}
      </div>
      {/* 시간 */}
      <div className="text-lg font-semibold">
        {date.toLocaleTimeString()} {/* 시간만 표시 */}
      </div>
    </div>
  );
}
