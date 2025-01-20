import React from 'react'
import Sidebar from '../PageElement/Sidebar';
import { Routes, Route } from 'react-router-dom';
import Page from '../Pages/Page';
import Page2 from '../Pages/Page2'
import Page3 from '../Pages/Page3'

export default function AdminPage() {
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
                dddd
              </div>

              {/* 컨텐츠 스크린 */}
              <div className='flex-1 bg-gray-200 overflow-auto '>
                <Routes>
                  {/* 다른 경로도 필요하면 여기에 추가 */}
                  <Route path="page" element={<Page />} />
                  <Route path="page2" element={<Page2 />} />
                  <Route path="page3" element={<Page3 />} />
                </Routes>
              </div>
              
          </div>

                                
       
    </div>
  )
}
