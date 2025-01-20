import React from 'react';
import ChartBar from '../Util/ChartBar'

export default function Page() {
  return (
    <div className="pt-10 px-8 h-full">
      {/* 윗줄 */}
      <div className='flex justify-between space-x-4 h-3/5'>
        {/* 왼쪽섹션 */}
        <div className='flex-1
                       bg-green-500 p-5 rounded-lg shadow-lg'>
          <ChartBar />
        </div>
        {/* 오른쪽섹션 */}
        <div className='flex-1
                        bg-green-500 p-5 rounded-lg shadow-lg'>


          <div class="relative overflow-x-auto shadow-md sm:rounded-lg">
            <table class="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
              <thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                <tr>
                  <th scope="col" class="px-6 py-3">
                    Product name
                  </th>
                  <th scope="col" class="px-6 py-3">
                    Color
                  </th>
                  <th scope="col" class="px-6 py-3">
                    Category
                  </th>
                  <th scope="col" class="px-6 py-3">
                    Price
                  </th>

                </tr>
              </thead>
              <tbody>
                <tr class="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                  <th scope="row" class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                    Apple MacBook Pro 17"
                  </th>
                  <td class="px-6 py-4">
                    Silver
                  </td>
                  <td class="px-6 py-4">
                    Laptop
                  </td>
                  <td class="px-6 py-4">
                    $2999
                  </td>

                </tr>

              </tbody>
            </table>
          </div>

        </div>

      </div>

      {/* 아래 섹션 */}
      <div className='flex-1 h-1/3
                     bg-green-500 p-5 rounded-lg shadow-lg mt-5'>

      </div>
    </div>
  );
}
