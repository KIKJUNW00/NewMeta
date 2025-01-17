import React, { useState, useEffect } from 'react';

export default function Clock() {
    const [time, setTime] = useState(new Date().toLocaleString());

    useEffect(() => {
      // 1초마다 현재 시간을 업데이트하는 타이머 설정
      const timer = setInterval(() => {
        setTime(new Date().toLocaleString());
      }, 1000);
  
      // 컴포넌트가 언마운트될 때 타이머를 정리
      console.log("Clock rendered");//
      return () => clearInterval(timer); // 클리어 하기 전까지는 계속 돈다.
    }, []);
  
    return (
      <div>
        {time}
      </div>
    );
}
