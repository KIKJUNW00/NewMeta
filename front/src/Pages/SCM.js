import React from 'react';
import { BoardSCM } from '../Util/SCMarchiUtil/SCMBoard';
import SCMarchi from '../Util/DashBoardUtil/SCMarchi1';

export default function SCMDashboard() {

  return (
    <div className="h-screen bg-gray-100 p-4">

      {/* 메인 대시보드 영역 */}
      <div className="bg-white w-full h-full shadow-lg p-6 flex flex-col rounded">
        <div className="flex flex-1">
          {/* 왼쪽 필터/제품 카테고리 영역 */}
          <div className="w-[35%] border border-gray-300 bg-gray-50 p-4 mr-4 rounded">
            <h2 className="text-lg font-bold text-gray-700 mb-2">Product Category</h2>
            <BoardSCM />
          </div>

          {/* 오른쪽 메인 콘텐츠 영역 */}
          <div className="flex flex-col flex-1">
            {/* SCM 과정 */}
            <div className="flex flex-col flex-1 border border-gray-300 bg-gray-50 p-4 rounded">
              <h2 className="text-lg font-bold text-gray-700 mb-2">SCM 과정</h2>
              <div className="flex flex-col justify-center items-center flex-1">
                <SCMarchi />
              </div>
            </div>

            {/* 하단 두 개의 그래프 영역 */}
            <div className="flex mt-4 space-x-4">
              <div className="w-1/2 border border-gray-300 bg-gray-50 p-4 rounded">
                <h2 className="text-lg font-bold text-gray-700 mb-2">그래프</h2>
                {/* 실제 차트 컴포넌트로 교체 가능 */}
                <div className="flex-1 flex justify-center items-center h-full">
                  <p>차트 영역</p>
                </div>
              </div>
              <div className="w-1/2 border border-gray-300 bg-gray-50 p-4 rounded">
                <h2 className="text-lg font-bold text-gray-700 mb-2">허브별 이상치 그래프</h2>
                {/* 실제 차트 컴포넌트로 교체 가능 */}
                <div className="flex-1 flex justify-center items-center h-full">
                  <p>허브 이상치 차트 영역</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 추가: 최근 SCM 이벤트 패널 */}
        <div className="mt-4 border border-gray-300 bg-gray-50 p-4 rounded">
          <h2 className="text-lg font-bold text-gray-700 mb-2">최근 SCM 이벤트</h2>
          {/* 예시 데이터로 구성된 이벤트 테이블 */}
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th className="px-4 py-2 text-left text-sm font-medium text-gray-500">시간</th>
                  <th className="px-4 py-2 text-left text-sm font-medium text-gray-500">이벤트</th>
                  <th className="px-4 py-2 text-left text-sm font-medium text-gray-500">상세</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                <tr>
                  <td className="px-4 py-2">2025-03-01 12:34</td>
                  <td className="px-4 py-2">이상치 감지</td>
                  <td className="px-4 py-2">허브 Busan</td>
                </tr>
                <tr>
                  <td className="px-4 py-2">2025-03-02 09:20</td>
                  <td className="px-4 py-2">SCM 과정 업데이트</td>
                  <td className="px-4 py-2">제품 Category 변경</td>
                </tr>
                {/* 추가 이벤트는 실제 데이터를 기반으로 동적으로 렌더링 */}
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  );
}
