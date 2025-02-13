import React, { useState, useEffect, useContext } from 'react'
import { useNavigate } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import logo1 from '../../img/logo1.png';
import logout from '../../img/logout.png';
import Clock from '../DashBoardUtil/Clock';
import { AnomalyContext } from '../OutlierUtil/AnomalyContext';

export default function Sidebar() {

  const { newAnomaly, setNewAnomaly } = useContext(AnomalyContext);

  const navigate = useNavigate();

  // 로그인 상태 관리
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  // 컴포넌트 마운트 시 로그인 상태 확인
  useEffect(() => {
    if (localStorage.getItem('authToken')) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
    }
  }, []);

  // 로그아웃 처리 함수
  const handleLogout = () => {
    localStorage.removeItem('authToken');
    alert('로그아웃 완료')
    setIsLoggedIn(false);
    navigate('/');
  }

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


  return (
    <div className="flex flex-col h-full">
      {/* 상단 로고 */}
      <div className="flex justify-center mt-10 mb-20">
        <img src={logo1}
          alt="Logo"
          className="w-[90%] object-contain" // 로고를 가로로 작게 설정
        />
      </div>

      {/* 프로필 */}
      <div className='flex items-center justify-center
                      text-white'>

        {user.username ? `${user.username}` : ''}

      </div>


      {/* 중간 메뉴 */}
      <div className="flex-1">
        
        {/* 1번 컨텐츠 */}
        <div className="bg-[#2e3b4e] transition-colors hover:bg-[#3a4a63] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/dashboard"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#3a4a63] w-full h-full flex items-center justify-center'
                : 'w-full h-full flex items-center justify-center'
            }
          >
            <p className='text-white  text-xl'
            >
              HOME
            </p>
          </NavLink>
        </div>

        {/* 2번 컨텐츠 */}
        <div className="bg-[#2e3b4e] transition-colors hover:bg-[#3a4a63] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/scm"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#3a4a63] w-full h-full flex items-center justify-center'
                : 'w-full h-full flex items-center justify-center'
            }
          >
            <p className='text-white  text-xl'
            >
              SCM
            </p>
          </NavLink>
        </div>

        {/* 3번 컨텐츠 */}
        <div className="bg-[#2e3b4e] transition-colors hover:bg-[#3a4a63] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/outlier"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#3a4a63] w-full h-full flex items-center justify-center'
                : 'w-full h-full flex items-center justify-center'
            }
            onClick={() => setNewAnomaly(false)}
          >
            <div className="relative flex items-center">
              <p className="text-white text-xl">
                이상치
                {newAnomaly && <span className="ml-2 text-red-500 font-bold">!</span>}
              </p>
            </div>
          </NavLink>

        </div>

        <div className="bg-[#2e3b4e] transition-colors hover:bg-[#3a4a63] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/mypage"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#3a4a63] w-full h-full flex items-center justify-center'
                : 'w-full h-full flex items-center justify-center'
            }
          >
            <p className='text-white  text-xl'
            >
              마이페이지
            </p>
          </NavLink>
        </div>

      </div>

      {/* 로그아웃 */}
      <div className="flex justify-center mb-5
                      bg-[#2e3b4e] transition-colors hover:bg-[#3a4a63] items-center w-full h-16">

        {isLoggedIn && (
          <img
            src={logout} // 로그아웃 아이콘 경로
            alt="로그아웃"
            className="w-8 h-8 cursor-pointer"
            onClick={handleLogout} // 클릭 시 로그아웃
          />
        )}
      </div>

      {/* 하단 Clock */}
      <div className="flex justify-center text-xl font-bold text-white mb-5">
        <Clock />
      </div>
    </div>
  );
}
