import React from 'react';
import ReactFlow, { Background, Controls } from 'reactflow';
import 'reactflow/dist/style.css';
import '../../reactflow.css';

export default function SCMarchi({ epcCode, events = [] }) {
  const isDomestic = epcCode.startsWith('001.880');

  // 특정 이벤트 유형의 발생 시간을 가져오는 함수
  const getEventTimes = (eventType) => {
    const matchedEvents = events.filter(
      (e) => e.eventType?.trim().toLowerCase() === eventType.trim().toLowerCase()
    );
    return matchedEvents.length > 0 ? matchedEvents.map((e) => e.eventTime) : ["시간 정보 없음"];
  };

  // 📌 특정 이벤트가 데이터에 없거나, 이상치인지 확인
  const isAnomalous = (eventType) => {
    const exists = events.some(
      (e) => e.eventType?.trim().toLowerCase() === eventType.trim().toLowerCase()
    );
    const anomaly = events.some(
      (e) =>
        e.eventType?.trim().toLowerCase() === eventType.trim().toLowerCase() &&
        e.isAnomaly === true
    );
    return !exists || anomaly; // 🔥 데이터가 없거나 이상 데이터면 붉게 표시
  };

  // 노드 생성 함수
  const createNode = (id, eventType, position = { x: 0, y: 0 }) => {
    const eventTimes = getEventTimes(eventType);
    const anomalous = isAnomalous(eventType);

    return {
      id: id,
      data: {
        label: (
          <div className="text-center transition-transform transform hover:scale-105 cursor-pointer">
            <div className="text-base font-bold">{eventType}</div>
            <div className="text-xs text-gray-500 mt-1">
              {eventTimes.length > 0 ? eventTimes.join(", ") : "시간 정보 없음"}
            </div>
          </div>
        ),
      },
      position,
      style: {
        width: '260px',
        backgroundColor: anomalous ? '#f87171' : '#f9fafb', // 🔥 데이터 없음 or 이상치면 붉게
        padding: '16px',
        borderRadius: '16px',
        border: anomalous ? '2px solid #dc2626' : '1px solid #d1d5db', // 🔥 붉은 테두리
        boxShadow: anomalous
          ? '0 4px 12px rgba(239, 68, 68, 0.8)' // 🔥 강한 붉은 그림자
          : '0 4px 8px rgba(0, 0, 0, 0.15)',
        textAlign: 'center',
        transition: 'transform 0.2s ease-in-out',
        fontSize: '16px',
        fontWeight: '600',
        color: '#1f2937',
        lineHeight: '1.5',
      },
    };
  };

  // 📌 국내산 & 수입산의 SCM 흐름 정의
  const nodes = isDomestic
    ? [
        createNode('2', 'Commissioning', { x: 250, y: 150 }),
        createNode('4', 'Aggregation', { x: 250, y: 250 }),
        createNode('6', 'WMS_inbound', { x: 750, y: 220 }),
        createNode('7', 'WMS_outbound', { x: 750, y: 320 }),
        createNode('8', 'stock_inbound(HUB)', { x: 250, y: 450 }),
        createNode('9', 'stock_outbound(HUB)', { x: 250, y: 550 }),
        createNode('10', 'stock_inbound(Wholesaler)', { x: 750, y: 500 }),
        createNode('11', 'stock_outbound(Sell)', { x: 750, y: 600 }),
      ]
    : [
        createNode('2', 'Custom Inbound', { x: 450, y: 130 }),
        createNode('3', 'Custom Outbound', { x: 450, y: 280 }),
        createNode('9', 'stock_inbound(HUB)', { x: 100, y: 300 }),
        createNode('10', 'stock_outbound(HUB)', { x: 100, y: 400 }),
        createNode('11', 'stock_inbound(Wholesaler)', { x: 700, y: 450 }),
        createNode('12', 'stock_outbound(Sell)', { x: 700, y: 550 }),
      ];

  // 📌 노드 간 연결 설정 (연결 방식: step)
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
      <h2 className="text-center text-lg font-bold mb-4">
        SCM 과정 흐름 ({isDomestic ? '국내산' : '수입산'})
      </h2>
      <div className="flex-1">
        <ReactFlow nodes={nodes} edges={edges} fitView style={{ height: '100%' }}>
          <Background />
          <Controls />
        </ReactFlow>
      </div>
    </div>
  );
}
