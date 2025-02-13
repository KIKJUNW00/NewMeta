// src/Pages/Outlier.js
import React from 'react';
import OutlierBoard from '../Util/OutlierUtil/OutlierBoard';
import Chart2 from '../Util/OutlierUtil/Chart2';
import AnomalyTypeCircleChart from '../Util/OutlierUtil/AnomalyTypeCircleChart';
import HubCircleChart from '../Util/OutlierUtil/HubCircleChart';

export default function Outlier({setNewAnomaly }) {
  return (
    <div className="h-screen overflow-auto bg-gray-100 p-4 flex items-center justify-center">
      {/* 전체 페이지 컨테이너: 화면 전체 높이, 회색 배경, 여백을 충분히 줌 */}
      <div className="bg-white w-full h-full overflow-auto shadow-lg p-4 flex flex-col space-y-4">
        {/* 위쪽 섹션: 두 개의 영역을 좌우로 배치하고 일정 간격(gap)을 줌 */}
        <div className="flex flex-1 justify-between gap-4">
          {/* 첫 번째 영역: 이상치 리스트 */}
          <div className="flex flex-1 flex-col border border-gray-300 bg-gray-50 rounded-lg p-6 shadow">
            <h2 className="text-lg font-bold text-gray-700 mb-4">이상치 리스트</h2>
            <OutlierBoard setNewAnomaly={setNewAnomaly}/>
          </div>
          {/* 두 번째 영역: 이상치 그래프 */}
          <div className="flex flex-1 flex-col border border-gray-300 bg-gray-50 rounded-lg p-6 shadow">
            <h2 className="text-lg font-bold text-gray-700 mb-2">이상치 그래프</h2>
            {/* 그래프 컴포넌트 등 추가 */}
            <Chart2 />
          </div>
        </div>

        {/* 아랫쪽 섹션: 두 개의 영역을 좌우로 배치 */}
        <div className="flex flex-1 gap-4">
          {/* 첫 번째 영역: 이상치 이벤트별 그래프 */}
          <div className="flex flex-1 flex-col border border-gray-300 bg-gray-50 rounded-lg p-6 shadow">
            <h2 className="text-lg font-bold mb-5 text-gray-700 ">이상치 유형별 차트</h2>
            {/* 이벤트별 그래프 컴포넌트를 추가 */}
            <AnomalyTypeCircleChart />
          </div>
          {/* 두 번째 영역: 지역별 이상치 갯수 */}
          <div className="flex flex-1 flex-col border border-gray-300 bg-gray-50 rounded-lg p-6 shadow">
            <h2 className="text-lg font-bold mb-5 text-gray-700 ">허브별 이상치 발생 수</h2>
            {/* 지역별 이상치 갯수를 표시하는 컴포넌트를 추가*/}
            <HubCircleChart />
          </div>
        </div>
      </div>
    </div>
  );
}
