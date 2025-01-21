import React from 'react';


export default function Page() {

  return (
    
    <div className="pt-10 px-8 h-full">
      {/* 윗줄 */}
      <div className='flex justify-between space-x-4 h-3/5'>
        {/* 왼쪽섹션 */}
        <div className='flex-1
                       bg-gray-50 p-5 rounded-lg shadow-lg'>
          hujlj
        </div>
        {/* 오른쪽섹션 */}
        <div className='flex-1
                        bg-gray-50 p-5 rounded-lg shadow-lg'>

        dsdsa

         
        </div>

      </div>

      {/* 아래 섹션 */}
      <div className='flex-1 h-1/3 
                     bg-gray-50 p-5 rounded-lg shadow-lg mt-5'>
          hjk
      </div>
    </div>
    
  );
}
