import React from 'react'

// 대시보드 표
export function Board({ onProductClick }) {


    const productData = [
        {
            name: 'Apple MacBook Pro 17"',
            color: 'Silver',
            category: 'Laptop',
            price: '$2999',
            epcCode: 'EPC001',
            readPoints: [
                [37.5665, 126.9780], // 서울
                [31.2304, 121.4737], // 상하이
            ],
        },
        {
            name: 'Dell XPS 13',
            color: 'Black',
            category: 'Laptop',
            price: '$1999',
            epcCode: 'EPC002',
            readPoints: [
                [35.6895, 139.6917], // 도쿄
                [39.9042, 116.4074], // 베이징
            ],
        },
    ];

    return (
        <div className="relative overflow-x-auto mt-2 border-2 border-black">
            <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                    <tr>
                        <th className="px-6 py-3">Product Name</th>
                        <th className="px-6 py-3">Color</th>
                        <th className="px-6 py-3">Category</th>
                        <th className="px-6 py-3">Price</th>
                    </tr>
                </thead>
                <tbody>
                    {productData.map((product, index) => (
                        <tr
                            key={index}
                            className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                            onClick={() => onProductClick(product.readPoints)} // 클릭 시 읽기 좌표 전달
                        >
                            <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                                {product.name}
                            </th>
                            <td className="px-6 py-4">{product.color}</td>
                            <td className="px-6 py-4">{product.category}</td>
                            <td className="px-6 py-4">{product.price}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

// 대시보드 이상치 테이블
export function BoardX() {
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

