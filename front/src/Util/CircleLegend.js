import React from 'react'

export default function CircleLegend() {
    return (
        <div className='flex'>
            <div className="flex mr-2 items-center">
                {/* 원 모양으로 색상 적용 */}
                <span
                    className="mr-2 w-4 h-4 rounded-full"
                    style={{ backgroundColor: 'rgba(54, 162, 235, 0.5)' }}
                />
                <span>Korea</span>
            </div>
            <div className="flex items-center">
                <span
                    className="mr-2 w-4 h-4 rounded-full"
                    style={{ backgroundColor: 'rgba(255, 99, 132, 0.5)' }}
                />
                <span>China</span>
            </div>
        </div>
    )
}
