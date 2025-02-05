import React, { useState, useEffect } from 'react'
import { useLocation } from 'react-router-dom';
import Sidebar from '../Util/DashBoardUtil/Sidebar';
import { Routes, Route } from 'react-router-dom';
import DashBoard from '../Pages/DashBoard';
import SCM from './SCM'
import Page3 from '../Pages/Page3'

export default function AdminPage() {

  // 관리자이름 가져오기
  const location = useLocation();
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('userData');
    return savedUser ? JSON.parse(savedUser) : location.state?.userData || { username: '' };
  });

  useEffect(() => {
    if (!user.username) {
      const savedUser = localStorage.getItem('userData');
      if (savedUser) {
        const parsedUser = JSON.parse(savedUser);
        setUser(parsedUser); // localStorage에서 가져온 값으로 업데이트
        console.log('로컬 스토리지에서 유저 데이터 로드:', parsedUser);
      } else {
        console.log('유저 데이터가 없습니다. 로그인 필요.');
      }
    }
  }, [user.username]);


  // -------------------------------------------------------------------------

  return (
    <div className='h-screen bg-white flex'>
      {/* Sidebar */}
      <div className='h-full w-1/12 
                      bg-[#2e3b4e]'>
        <Sidebar />
      </div>

      {/* 메인 컨텐츠 */}
      <div className='flex flex-col w-full h-full'>

        {/* 컨텐츠 스크린 */}
        <div className='flex-1 h-full
                       bg-gray-200 overflow-auto '>

          <Routes>
            {/* 다른 경로도 필요하면 여기에 추가 */}
            <Route path="dashboard" element={<DashBoard />} />
            <Route path="scm" element={<SCM />} />
            <Route path="page3" element={<Page3 />} />
          </Routes>
        </div>

      </div>



    </div>
  )
}
