import React from 'react'
import { useState, useEffect } from "react";

// SCM 과정 테이블
export function BoardSCM(onProductClick) {
    const [productData, setProductData] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState("");
    const itemsPerPage = 8;
  
    useEffect(() => {
      const fetchAllData = async () => {
        try {
          let allData = [];
          let page = 0;
          let totalPages = 1;
  
          while (page < totalPages) {
            const response = await fetch(`http://10.125.121.228:8080/producteventLog/paged?page=${page}&size=30`);
            const data = await response.json();
  
            if (data && Array.isArray(data.content)) {
              allData = [...allData, ...data.content];
              totalPages = data.totalPages;
              page += 1;
            } else {
              console.error("API 데이터 형식이 예상과 다릅니다.", data);
              break;
            }
          }
  
          console.log("📌 전체 데이터 개수 (API 응답):", allData.length);
  
          const uniqueDataMap = new Map();
  
          allData.forEach((item) => {
            if (!uniqueDataMap.has(item.epcCode)) {
              uniqueDataMap.set(item.epcCode, item);
            } else {
              const existingItem = uniqueDataMap.get(item.epcCode);
              if (!existingItem.latitude || !existingItem.longitude) {
                uniqueDataMap.set(item.epcCode, item);
              } else if (item.latitude && item.longitude) {
                uniqueDataMap.set(item.epcCode, item); 
              }
            }
          });
  
          setProductData(Array.from(uniqueDataMap.values()));
        } catch (error) {
          console.error("API 요청 오류:", error);
        }
      };
  
      fetchAllData();
    }, []);
  
    // 검색어에 따른 데이터 필터링 (대소문자 구분 없이)
    const filteredData = productData.filter(product =>
      product.epcCode.toLowerCase().includes(searchQuery.toLowerCase())
    );
  
    // 검색어 변경 시 페이지를 1로 리셋
    useEffect(() => {
      setCurrentPage(1);
    }, [searchQuery]);
  
    const totalFilteredPages = Math.ceil(filteredData.length / itemsPerPage);
    const paginatedData = filteredData.slice(
      (currentPage - 1) * itemsPerPage,
      currentPage * itemsPerPage
    );
  
    return (
      <>
        {/* 검색 입력 필드 */}
        <div className="mb-4">
          <input
            type="text"
            placeholder="Search EPC Code..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="border border-gray-300 p-2 rounded w-full"
          />
        </div>
  
        <div className="relative overflow-x-auto mt-2 border border-black">
          <table className="w-full min-w-[300px] text-sm text-left text-gray-500 dark:text-gray-400">
            <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
              <tr>
                <th className="px-6 py-3">No</th>
                <th className="px-6 py-3">Product Name</th>
                <th className="px-6 py-3">EPC Code</th>
              </tr>
            </thead>
            <tbody>
              {paginatedData.map((product, index) => (
                <tr 
                  key={product.epcCode} 
                  className="cursor-pointer hover:bg-gray-100"
                  onClick={() => onProductClick(product.epcCode)}
                >
                  <td className="px-6 py-4 truncate">{index + 1}</td>
                  <td className="px-6 py-4 truncate">{product.productName}</td>
                  <td className="px-6 py-4 truncate">{product.epcCode}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
  
        <div className="flex justify-center mt-2.5">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
            className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
          >
            Prev
          </button>
          <span className="px-4 py-2">Page {currentPage} of {totalFilteredPages}</span>
          <button
            onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalFilteredPages))}
            disabled={currentPage === totalFilteredPages}
            className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </>
    )
}


