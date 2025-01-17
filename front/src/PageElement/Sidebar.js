import React from 'react';
import { NavLink } from 'react-router-dom';
import logo1 from '../img/logo1.png';
import Clock from '../PageElement/Clock';

export default function Sidebar() {
  return (
    <div className="flex flex-col h-full">
      {/* 상단 로고 */}
      <div className="flex justify-center mt-10 mb-20">
        <img src={logo1} alt="Logo"></img>
      </div>

      {/* 중간 메뉴 */}
      <div className="flex-1">
        {/* 1번 컨텐츠 */}
        <div className="bg-[#8297a1] text-white text-4xl transition-colors hover:bg-[#A5BFCC] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/page"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#A5BFCC] text-white w-full h-full flex items-center justify-center'
                : 'text-white w-full h-full flex items-center justify-center'
            }
          >
            1
          </NavLink>
        </div>

        {/* 2번 컨텐츠 */}
        <div className="bg-[#8297a1] text-white text-4xl transition-colors hover:bg-[#A5BFCC] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/page2"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#A5BFCC] text-white w-full h-full flex items-center justify-center'
                : 'text-white w-full h-full flex items-center justify-center'
            }
          >
            2
          </NavLink>
        </div>

        {/* 3번 컨텐츠 */}
        <div className="bg-[#8297a1] text-white text-4xl transition-colors hover:bg-[#A5BFCC] flex items-center w-full h-16">
          <NavLink
            to="/AdminPage/page3"
            className={({ isActive }) =>
              isActive
                ? 'bg-[#A5BFCC] text-white w-full h-full flex items-center justify-center'
                : 'text-white w-full h-full flex items-center justify-center'
            }
          >
            3
          </NavLink>
        </div>
      </div>

      {/* 하단 Clock */}
      <div className="flex justify-center text-xl font-bold mb-5">
        <Clock />
      </div>
    </div>
  );
}
