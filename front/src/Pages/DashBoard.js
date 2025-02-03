import React, { useState } from 'react';
import MapComponent from '../Util/MapComponent';
import MapComponent2 from '../Util/MapComponent2'
import Chart1 from '../Util/Chart1'
import CircleLegend from '../Util/CircleLegend';
import {Board} from '../Util/Boards';
import {BoardX} from '../Util/Boards'

export default function DashBoard() {

  const [readPoints, setReadPoints] = useState([]); // 선택된 readPoints 관리

  return (
    <div className="h-screen bg-gray-100 flex items-center justify-center">
      {/* 전체 배경 */}
      <div className="bg-white w-[98%] h-[97%] rounded-lg shadow-lg p-6 flex flex-col space-y-4">
        {/* 윗 섹션 */}
        <div className="flex space-x-4 h-1/2">

          {/* 윗쪽 왼쪽 섹션 국가별 지도 product 분포표 한국/중국 */}
          <div className="flex w-[40%] h-full border-solid border-2 border-black
                         bg-gray-50 p-4 shadow">
            <div className='w-full h-full flex flex-col'>
              <div className='flex justify-between pb-2'>
                <h2 className="text-lg font-bold text-gray-700">HUB별 물품량</h2>
                <div className="flex flex-col space-y-2">
                  <CircleLegend />
                </div>
              </div>
              <div className="flex border-solid border-2 border-black
                              overflow-hidden">
                <MapComponent />
              </div>
            </div>
          </div>

          {/* 윗쪽 오른쪽 섹션 일별이상치, 월별 이상치, 연도별 이상치 */}
          <div className="flex-1 border-solid border-2 border-black
                        bg-gray-50 p-4 shadow">
            <h2 className="text-lg font-bold text-gray-700">날짜별 이상치</h2>

            <Chart1 />
          </div>

        </div>

        {/* 아랫 섹션 */}
        <div className="flex space-x-4 h-1/2">

          {/* 왼쪽 아래 epc데이터 */}
          <div className="flex-1 border-solid border-2 border-black
                        bg-gray-50 p-4 shadow">

            <div className='flex-1 flex-col'>
              <h2 className="text-lg font-bold text-gray-700">EPC데이터</h2>

              <Board onProductClick={(points) => setReadPoints(points)} />
            </div>

          </div>

          {/* 오른쪽중간 epc데이터 누르면 scm과정 */}
          <div className="flex-1 border-solid border-2 border-black
                        bg-gray-50 p-4 shadow">
            <div className='flex flex-col '>
              <h2 className="text-lg font-bold text-gray-700">SCM</h2>
              <div className="flex-1 border-solid border-2 border-black
                              overflow-hidden">

                <MapComponent2 readPoints={readPoints} />
              </div>
            </div>

          </div>

          {/* 오른쪽 아래 이상치 리스트만 띄우기 */}
          <div className="flex-1 border-solid border-2 border-black
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
