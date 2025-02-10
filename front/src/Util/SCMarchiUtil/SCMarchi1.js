import React from 'react';
import ReactFlow, { Background, Controls } from 'reactflow';
import 'reactflow/dist/style.css';
import '../../reactflow.css'

export default function SCMarchi({ epcCode, events = [] }) {
  const isDomestic = epcCode.startsWith('001.880');

  const getEventTimes = (eventType) => {
    const matchedEvents = events.filter((e) =>
      e.eventType?.trim().toLowerCase() === eventType.trim().toLowerCase()
    );
    if (matchedEvents.length === 0) {
      console.warn(`⚠️ ${eventType} 이벤트가 데이터에 없습니다.`);
    }
    const uniqueTimes = [...new Set(matchedEvents.map((e) => e.eventTime))];
    return uniqueTimes.length > 0 ? uniqueTimes : ["시간 정보 없음"];
  };



  const createNode = (id, label, eventType, position = { x: 0, y: 0 }) => {
    const safePosition = {
      x: isNaN(position.x) ? 0 : position.x,
      y: isNaN(position.y) ? 0 : position.y,
    };


    const eventTimes = getEventTimes(eventType);
    return {
      id: id,
      data: {
        label: (
          <div className=" text-center transition-transform transform hover:scale-105 cursor-pointer">
            <div className="text-base font-bold" >{eventType}</div>

            <div className="text-xs text-gray-500 mt-1">
              {eventTimes.length > 0 ? eventTimes.join(", ") : "시간 정보 없음"}
            </div>
          </div>

        ),
      },
      position: safePosition,
      style: {
        width: '260px',
        backgroundColor: '#f9fafb',  // 더 밝은 배경색
        padding: '16px',
        borderRadius: '16px',
        border: '1px solid #d1d5db',  // 더 진한 테두리 색상
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.15)',  // 더 부드러운 그림자
        textAlign: 'center',
        transition: 'transform 0.2s ease-in-out',
        fontSize: '16px',  // 글자 크기 증가
        fontWeight: '600',  // 글자 두께 증가
        color: '#1f2937',  // 어두운 색 글자
        lineHeight: '1.5',  // 줄 간격 증가
      }

    };
  };

  const nodes = isDomestic
    ? [
      createNode('2', 'Commissioning', 'Commissioning', { x: 250, y: 150 }),
      createNode('4', 'Aggregation', 'aggregation', { x: 250, y: 250 }),
      createNode('6', 'WMS Inbound', 'WMS_inbound', { x: 750, y: 220 }),
      createNode('7', 'WMS Outbound', 'WMS_outbound', { x: 750, y: 320 }),
      createNode('8', 'Stock Inbound (HUB)', 'stock_inbound(HUB)', { x: 250, y: 450 }),
      createNode('9', 'Stock Outbound (HUB)', 'stock_outbound(HUB)', { x: 250, y: 550 }),
      createNode('10', 'Stock Inbound (Wholesaler)', 'stock_inbound(Wholesaler)', { x: 750, y: 500 }),
      createNode('11', 'Stock Outbound (Sell)', 'stock_outbound(Sell)', { x: 750, y: 600 }),
    ]
    : [
      createNode('2', 'Custom Inbound', 'custom_inbound', { x: 450, y: 130 }),
      createNode('3', 'Custom Outbound', 'custom_outbound', { x: 450, y: 280 }),
      createNode('9', 'Stock Inbound (HUB)', 'stock_inbound(HUB)', { x: 100, y: 300 }),
      createNode('10', 'Stock Outbound (HUB)', 'stock_outbound(HUB)', { x: 100, y: 400 }),
      createNode('11', 'Stock Inbound (Wholesaler)', 'stock_inbound(Wholesaler)', { x: 700, y: 450 }),
      createNode('12', 'Stock Outbound (Sell)', 'stock_outbound(Sell)', { x: 700, y: 550 }),
    ];

  const edges = nodes
    .map((node, index) => {
      if (index < nodes.length - 1) {
        return {
          id: `e${nodes[index].id}-${nodes[index + 1].id}`,
          source: nodes[index].id,
          target: nodes[index + 1].id,
          animated: true,
          type: 'step',
          style: { stroke: '#333' },
        };
      }
      return null;
    })
    .filter(Boolean);

  return (
    <div className="flex flex-col w-full h-full border border-gray-300 rounded-lg p-2 shadow-md bg-white">
      <h2 className="text-center text-lg font-bold mb-4">SCM 과정 흐름 ({isDomestic ? '국내산' : '수입산'})</h2>
      <div className="flex-1">
        <ReactFlow nodes={nodes} edges={edges} fitView style={{ height: '100%' }}>
          <Background />
          <Controls />
        </ReactFlow>
      </div>
    </div>
  );
}
