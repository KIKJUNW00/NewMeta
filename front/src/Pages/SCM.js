import React from 'react';
import { BoardSCM } from '../Util/Boards';
import SCMarchi from '../Util/SCMarchi1';

export default function SCM() {
  return (
    <div className='h-screen bg-gray-100 flex justify-center'>

      <div className="bg-white w-[98%] h-[97%] shadow-lg p-6 
                      flex">

        <div className='w-[40%] h-full border-solid border-2 border-black
                         bg-gray-50 p-4 '>
          <h2 className="text-lg font-bold text-gray-700">날짜별 이상치</h2>
          <BoardSCM />

        </div>

        <div className='flex flex-col w-full h-full ml-5  '>
          <div className='flex w-full h-[60%] border-solid border-2 border-black bg-gray-50 justify-center items-center'>
            <h2 className="text-lg font-bold text-gray-700">날짜별 이상치</h2>
            <div className='flex flex-col justify-center items-center'>
              <SCMarchi />
            </div>
          </div>


          <div className='flex justify-between 
                           w-full h-[40%] mt-5 '>

            <div className='w-[49%] border-solid border-2 border-black
                         bg-gray-50'>
              <h2 className="text-lg font-bold text-gray-700">날짜별 이상치</h2>
            </div>
            <div className='w-[49%] border-solid border-2 border-black
                         bg-gray-50'>
              <h2 className="text-lg font-bold text-gray-700">날짜별 이상치</h2>
            </div>
          </div>

        </div>

      </div>
    </div>
  );
}
