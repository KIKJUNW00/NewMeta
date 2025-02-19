import React, { useState, useEffect } from 'react';
import MapComponent from '../Util/DashBoardUtil/MapComponent';
import MapComponent2 from '../Util/DashBoardUtil/MapComponent2'
// import Chart1 from '../Util/DashBoardUtil/Chart1'
// import CircleLegend from '../Util/DashBoardUtil/CircleLegend';
import { Board } from '../Util/DashBoardUtil/Boards';
import { BoardX } from '../Util/DashBoardUtil/Boards'
import AllPostsBoard from '../Util/DashBoardUtil/AllPostsBoard';

export default function DashBoard() {

  const [selectedEpcCode, setSelectedEpcCode] = useState(null);
  const [eventLogData, setEventLogData] = useState([]);

  useEffect(() => {
  const fetchAllEventLogs = async () => {
    try {
      let allEvents = [];
      let page = 0;
      let totalPages = 1;

      while (page < totalPages) {
        const response = await fetch(`http://localhost:8080/producteventLog/paged?page=${page}&size=30`);
        const data = await response.json();
        if (data && Array.isArray(data.content)) {
          allEvents = [...allEvents, ...data.content];
          totalPages = data.totalPages;
          page += 1;
        } else {
          console.error("API 데이터 형식 오류", data);
          break;
        }
      }
      setEventLogData(allEvents);
    } catch (error) {
      console.error("API 요청 오류:", error);
    }
  };

  fetchAllEventLogs();
}, []);


  return (
    <div className="h-screen bg-gray-100 flex items-center justify-center ">
      {/* 전체 배경 */}
      <div className="bg-white w-[98%] h-[97%] shadow-lg p-4 flex flex-col space-y-4">
        {/* 윗 섹션 */}
        <div className="flex space-x-4 h-1/2">

          {/* 윗쪽 왼쪽 섹션 HUB별 지도 product 분포표 */}
          <div className="flex w-[40%] h-full border-solid border border-gray-300
                         bg-gray-50 p-4 shadow">
            <div className='w-full h-full flex flex-col'>
              <div className='flex justify-between pb-2'>
                <h2 className="text-lg font-bold text-gray-700">HUB별 물품량</h2>
                {/* <div className="flex flex-col space-y-2">
                  <CircleLegend />
                </div> */}
              </div>
              <div className="flex border-solid border border-gray-600 rounded
                              overflow-hidden">
                <MapComponent />
              </div>
            </div>
          </div>

          {/* 윗쪽 오른쪽 섹션 일별이상치, 월별 이상치, 연도별 이상치 */}
          <div className="flex-1 border rounded border-gray-300
                        bg-gray-50 p-4 shadow">
            <h2 className="text-lg font-bold text-gray-700">전체 피드백 게시판</h2>
                    
            <AllPostsBoard/>
          </div>

        </div>

        {/* 아랫 섹션 */}
        <div className="flex space-x-4 h-1/2">

          {/* 왼쪽 아래 epc데이터 */}
          <div className="flex-1 border-solid border border-gray-300 rounded
                        bg-gray-50 p-4 shadow">

            <div className='flex-1 flex-col'>
              <h2 className="text-lg font-bold text-gray-700">EPC데이터</h2>

              <Board onProductClick={setSelectedEpcCode} />
            </div>

          </div>

          {/* 오른쪽중간 epc데이터 누르면 scm과정 */}
          <div className="flex-1 border-solid border border-gray-300 rounded
                        bg-gray-50 p-4 shadow">
            <div className='flex flex-col '>
              <h2 className="text-lg font-bold text-gray-700">SCM</h2>
              <div className="flex-1 border-solid border border-gray-600 rounded
                              overflow-hidden">

                <MapComponent2 readPoints={eventLogData} selectedEpcCode={selectedEpcCode} />
              </div>
            </div>

          </div>

          {/* 오른쪽 아래 이상치 리스트만 띄우기 */}
          <div className="flex-1 border-solid border border-gray-300 rounded
                        bg-gray-50 p-4 shadow">

            <div className='flex-1 flex-col'>
              <h2 className="text-lg font-bold text-gray-700">이상치 리스트</h2>
              <BoardX />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
