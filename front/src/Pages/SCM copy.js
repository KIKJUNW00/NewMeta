import React, { useState } from 'react';
import { BoardSCM } from '../Util/SCMarchiUtil/SCMBoard';
import SCMarchi from '../Util/SCMarchiUtil/SCMarchi1';

export default function SCMDashboard() {
  // 선택한 제품의 EPC 코드를 저장하는 상태
  const [selectedEpcCode, setSelectedEpcCode] = useState(null);

  // BoardSCM에서 제품을 클릭하면 호출되는 콜백
  const handleProductClick = (epcCode) => {
    setSelectedEpcCode(epcCode);
  };

  return (
    <div className="w-screen h-screen bg-gray-100 p-4 flex justify-center items-center">
      <div className="bg-white w-full h-full shadow-lg p-6 flex flex-col rounded">
        <div className="flex flex-1">
          {/* 왼쪽: Product Category 영역 (검색/필터 포함) */}
          <div className="w-[35%] border border-gray-300 bg-gray-50 p-4 mr-4 rounded overflow-y-auto">
            <h2 className="text-lg font-bold text-gray-700 mb-2">Product Category</h2>
            {/* onProductClick prop 전달 */}
            <BoardSCM onProductClick={handleProductClick} />
          </div>

          {/* 오른쪽: SCM 과정 영역 */}
          <div className="flex flex-col flex-1">
            {selectedEpcCode ? (
              <SCMarchi epcCode={selectedEpcCode} />
            ) : (
              <div className="flex-1 border border-gray-300 bg-gray-50 p-4 rounded flex justify-center items-center">
                <p className="text-gray-500">제품을 선택하면 SCM 과정을 확인할 수 있습니다.</p>
              </div>
            )}
          </div>
        </div>
        {/* 추가 영역 (예: 하단 차트나 이벤트 패널 등 필요시 추가) */}
      </div>
    </div>
  );
}
