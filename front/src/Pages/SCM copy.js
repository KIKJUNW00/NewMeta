import React, { useState } from 'react';
import { BoardSCM } from '../Util/SCMarchiUtil/SCMBoard';
import SCMarchi from '../Util/SCMarchiUtil/SCMarchi1';

export default function SCMDashboard() {

  // 선택한 제품의 EPC 코드를 저장하는 상태
  const [selectedEpcCode, setSelectedEpcCode] = useState(null);
  const [selectedEvents, setSelectedEvents] = useState([]);

  // BoardSCM에서 제품을 클릭하면 호출되는 콜백
  const handleProductClick = (epcCode, events) => {
    console.log("📡 제품 클릭:", epcCode);
    console.log("📋 같은 EPC 코드의 모든 이벤트:", events);

    setSelectedEpcCode(epcCode);
    setSelectedEvents(events); // 모든 이벤트 목록 저장
  };


  return (
    <div className="h-screen bg-gray-100 p-4">

      {/* 메인 대시보드 영역 */}
      <div className="bg-white w-full h-full shadow-lg p-6 flex flex-col rounded">
        <div className="flex flex-1">
          {/* 왼쪽 필터/제품 카테고리 영역 */}
          <div className="w-[35%] border border-gray-300 bg-gray-50 p-4 mr-4 rounded">
            <h2 className="text-lg font-bold text-gray-700 mb-2">Product Category</h2>
            <BoardSCM onProductClick={handleProductClick} />
          </div>

          {/* 오른쪽 메인 콘텐츠 영역 */}
          <div className="flex flex-col flex-1">
            {/* SCM 과정 */}
            <div className="flex flex-col flex-1 border border-gray-300 bg-gray-50 p-4 rounded">
              <h2 className="text-lg font-bold text-gray-700 mb-2">SCM 과정</h2>
              <div className="flex flex-col justify-center items-center flex-1">
                {selectedEpcCode ? (
                  <SCMarchi epcCode={selectedEpcCode} events={selectedEvents} />
                ) : (
                  <div className="flex-1 flex justify-center items-center">
                    <p className="text-gray-500">제품을 선택하면 SCM 과정을 확인할 수 있습니다.</p>
                  </div>
                )}
              </div>
            </div>

          </div>
        </div>

        <div className="flex flex-wrap mt-4 -mx-2">
          <div className="w-full lg:w-1/3 px-2 mb-4">
            <div className="border border-gray-300 bg-gray-50 p-4 rounded min-h-[250px]">
              <h2 className="text-lg font-bold text-gray-700 mb-2">그래프 1</h2>
              <div className="flex-1 flex justify-center items-center h-full">
                <p>차트 영역</p>
              </div>
            </div>
          </div>

          <div className="w-full lg:w-1/3 px-2 mb-4">
            <div className="border border-gray-300 bg-gray-50 p-4 rounded min-h-[250px]">
              <h2 className="text-lg font-bold text-gray-700 mb-2">그래프 2</h2>
              <div className="flex-1 flex justify-center items-center h-full">
                <p>차트 영역</p>
              </div>
            </div>
          </div>

          <div className="w-full lg:w-1/3 px-2 mb-4">
            <div className="border border-gray-300 bg-gray-50 p-4 rounded min-h-[250px]">
              <h2 className="text-lg font-bold text-gray-700 mb-2">허브별 이상치 그래프</h2>
              <div className="flex-1 flex justify-center items-center h-full">
                <p>허브 이상치 차트 영역</p>
              </div>
            </div>
          </div>
        </div>


      </div>
    </div>
  );
}
