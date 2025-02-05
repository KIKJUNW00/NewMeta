import React from 'react'
import { useState, useEffect } from "react";

//대시보드 표
export function Board({ onProductClick }) {
    const [productData, setProductData] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;

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
                setProductData(allData);

                // productName 중복 제거
                const uniqueData = [];
                const seenProductNames = new Set();

                allData.forEach((item) => {
                    if (!seenProductNames.has(item.epcCode)) {
                        seenProductNames.add(item.epcCode);
                        uniqueData.push(item);
                    }
                });

                setProductData(uniqueData);
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
                                // onClick={() => {
                                //     console.log("클릭한 EPC 코드:", product.epcCode); // 디버깅
                                //     onProductClick(product.epcCode, productData); // EPC 코드 전달
                                // }}
                                onClick={() => onProductClick(product.epcCode)} // 클릭 시 epcCode 전달
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
                const response = await fetch("http://10.125.121.228:8080/anomalies");
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

    const totalPages = Math.ceil((productData.length || 1) / ITEMS_PER_PAGE);

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
            <div className="relative overflow-x-auto mt-2 border border-black max-w-full">
                <table className="w-full text-xs text-left text-gray-500 dark:text-gray-400 table-fixed">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                        <tr>
                            <th className="px-6 py-2 w-1/5">Product Name</th>
                            <th className="px-6 py-2 w-1/5">Anomaly Type</th>
                            <th className="px-6 py-2 w-1/5">Reason</th>
                            <th className="px-6 py-2 w-1/5">Timestamp</th>
                            <th className="px-6 py-2 w-1/5">Hub</th>
                        </tr>
                    </thead>
                    <tbody>
                        {displayedData.map((item) => (
                            <tr key={item.epcCode} className="bg-red-200 border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                                <th scope="row" className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white truncate">
                                    {item.anomalyProductName}
                                </th>
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

// SCM 과정 테이블
export function BoardSCM() {
    return (
        <div className="relative overflow-x-auto mt-2
                        border-2 border-black">
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
                    <tr class="bg-red-200 border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
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

    )
}

