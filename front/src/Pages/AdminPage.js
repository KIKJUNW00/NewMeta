import React, { useState, useEffect } from 'react'
import { useLocation } from 'react-router-dom';
import Sidebar from '../Util/Sidebar';
import { Routes, Route } from 'react-router-dom';
import DashBoard from '../Pages/DashBoard';
import Page2 from '../Pages/Page2'
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
          <div className='h-full w-2/12 
                      bg-[#708285]'> 
            <Sidebar />
          </div>

            {/* 메인 컨텐츠 */}
          <div className='flex flex-col w-full h-full'>

              {/* 상단 바 */}
              <div className='h-16 w-full mb-5 bg-[#A5BFCC] '>
              {user.username ? `안녕하세요, ${user.username}님!` : '로그인이 필요합니다.'}
              </div>

              {/* 컨텐츠 스크린 */}
              <div className='flex-1 h-full
                             bg-gray-200 overflow-auto '>

                <Routes>
                  {/* 다른 경로도 필요하면 여기에 추가 */}
                  <Route path="dashboard" element={<DashBoard />} />
                  <Route path="page2" element={<Page2 />} />
                  <Route path="page3" element={<Page3 />} />
                </Routes>
              </div>
              
          </div>

                                
       
    </div>
  )
}
