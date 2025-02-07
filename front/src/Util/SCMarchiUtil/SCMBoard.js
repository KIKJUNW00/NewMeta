import React, { useState, useEffect } from "react";

// SCM 과정 테이블
export function BoardSCM({ onProductClick }) {
  const [productData, setProductData] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchQuery, setSearchQuery] = useState("");
  const [originFilter, setOriginFilter] = useState("all"); // "all", "domestic", "imported"
  const itemsPerPage = 8;

  useEffect(() => {
    const fetchAllData = async () => {
      try {
        let allData = [];
        let page = 0;
        let totalPages = 1;

        while (page < totalPages) {
          const response = await fetch(
            `http://10.125.121.228:8080/producteventLog/paged?page=${page}&size=30`
          );
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

  // 검색어와 originFilter에 따른 데이터 필터링 (대소문자 구분 없이)
  const filteredData = productData.filter((product) => {
    const searchMatch = product.epcCode
      .toLowerCase()
      .includes(searchQuery.toLowerCase());
    let originMatch = true;
    // EPC 코드의 5번째 문자부터 3글자가 "880"이면 국내산으로 판단
    if (originFilter === "domestic") {
      originMatch = product.epcCode.slice(4, 7) === "880";
    } else if (originFilter === "imported") {
      originMatch = product.epcCode.slice(4, 7) !== "880";
    }
    return searchMatch && originMatch;
  });

  // 검색어 변경 시 페이지를 1로 리셋
  useEffect(() => {
    setCurrentPage(1);
  }, [searchQuery, originFilter]);

  const totalFilteredPages = Math.ceil(filteredData.length / itemsPerPage);
  const paginatedData = filteredData.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  return (
    <>
      {/* 검색 입력 필드와 국내산/수입산 버튼 */}
      <div className="mb-4 flex items-center space-x-2">
        <input
          type="text"
          placeholder="EPC CODE를 입력하시오."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="border border-gray-300 p-2 rounded w-[60%] h-10"
        />
        <button
          onClick={() => setOriginFilter("domestic")}
          className={`px-4 py-2 rounded text-sm h-10 w-[18%] ${originFilter === "domestic"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-700"
            }`}
        >
          국내산
        </button>
        <button
          onClick={() => setOriginFilter("imported")}
          className={`px-4 py-2 rounded text-sm h-10 w-[18%] ${originFilter === "imported"
              ? "bg-blue-500 text-white"
              : "bg-gray-200 text-gray-700"
            }`}
        >
          수입산
        </button>
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
        <span className="px-4 py-2">
          Page {currentPage} of {totalFilteredPages}
        </span>
        <button
          onClick={() =>
            setCurrentPage((prev) => Math.min(prev + 1, totalFilteredPages))
          }
          disabled={currentPage === totalFilteredPages}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </>
  );
}
