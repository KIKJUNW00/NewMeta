import React from 'react'
import { useState, useEffect } from "react";

//대시보드 표
export function Board({ onProductClick }) {
    const [productData, setProductData] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [selectedEpc, setSelectedEpc] = useState(null); //  선택된 EPC 코드 상태
    const itemsPerPage = 5;

    useEffect(() => {
        const fetchAllData = async () => {
            try {
                let allData = [];
                let page = 0;
                let totalPages = 1;

                while (page < totalPages) {
                    const response = await fetch(`http://localhost:8080/producteventLog/paged?page=${page}&size=30`);
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
                setProductData(allData);

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

    const totalPages = Math.ceil(productData.length / itemsPerPage);
    const paginatedData = productData.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    return (
        <>
            <div className="relative overflow-x-auto mt-2 border border-gray-300 rounded">
                <table className="w-full min-w-[300px] text-sm text-left text-gray-500 dark:text-gray-400">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-400 dark:bg-gray-700 dark:text-gray-400">
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
                                className={`cursor-pointer transition-colors duration-200 ${selectedEpc === product.epcCode
                                    ? "bg-blue-200" //  클릭한 행은 연한 파랑 유지
                                    : "hover:bg-gray-200" // 마우스를 올리면 회색
                                    }`}
                                onClick={() => {
                                    setSelectedEpc(product.epcCode); //  클릭 시 선택 상태 업데이트
                                    onProductClick(product.epcCode); //  부모 컴포넌트로 EPC 코드 전달
                                }}
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
                <span className='px-4 py-2'>Page {currentPage} of {totalPages}</span>
                <button
                    onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                    disabled={currentPage === totalPages}
                    className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
                >
                    Next
                </button>
            </div>
        </>
    );
}



// 대시보드 이상치 테이블
export function BoardX() {

    const [productData, setProductData] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const ITEMS_PER_PAGE = 5;

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch("http:/localhost:8080/anomalies/paged");
                const data = await response.json();
                console.log("Fetched data:", data);

                if (data && Array.isArray(data.content)) {
                    setProductData(data.content);
                } else {
                    console.error("Unexpected API response format:", data);
                }
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };
        fetchData();
    }, []);

    // 총 페이지 수 계산 (0이면 최소 1페이지 보장)
    const totalPages = Math.max(Math.ceil(productData.length / ITEMS_PER_PAGE), 1);


    const handlePageChange = (newPage) => {
        if (newPage > 0 && newPage <= totalPages) {
            setCurrentPage(newPage);
        }
    };

    const displayedData = productData.slice(
        (currentPage - 1) * ITEMS_PER_PAGE,
        currentPage * ITEMS_PER_PAGE
    );

    return (
        <>
            <div className="relative overflow-x-auto mt-2 border border-gray-300 max-w-full">
                <table className="w-full text-xs text-left text-gray-500 dark:text-gray-400 table-fixed">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-400 dark:bg-gray-700 dark:text-gray-400">
                        <tr>
                            <th className="px-6 py-2 w-1/5">Product Name</th>
                            <th className="px-6 py-2 w-1/5">Anomaly Type</th>
                            <th className="px-6 py-2 w-1/5">Reason</th>
                            <th className="px-6 py-2 w-1/5">Timestamp</th>
                            <th className="px-6 py-2 w-1/5">Hub</th>
                        </tr>
                    </thead>
                    <tbody>
                        {displayedData.map((item, index) => (
                            <tr key={index} className="bg-red-200 border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                                <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white truncate">
                                    {item.anomalyProductName}
                                </td>
                                <td className="px-6 py-4 truncate">{item.anomalyType}</td>
                                <td className="px-6 py-4 truncate">{item.reason}</td>
                                <td className="px-6 py-4 truncate">{item.anomalyTimestamp}</td>
                                <td className="px-6 py-4 truncate">{item.anomalyHub}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* 페이지 버튼*/}
            <div className="flex justify-center mt-2.5">
                <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
                >
                    Prev
                </button>
                <span className="px-4 py-2">Page {currentPage} of {totalPages}</span>
                <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
                >
                    Next
                </button>
            </div>
        </>
    )
}

