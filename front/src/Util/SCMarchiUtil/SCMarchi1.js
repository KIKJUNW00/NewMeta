import React from 'react';
import ReactFlow, { Background, Controls } from 'reactflow';
import 'reactflow/dist/style.css';
import '../../reportWebVitals';

export default function SCMarchi({ epcCode }) {
  // EPC 코드가 "001.880"로 시작하면 국내산, 아니면 수입산으로 판단
  const isDomestic = epcCode.startsWith('001.880');

  // 국내산 SCM 과정 노드 정의
  const nodes = isDomestic
    ? [
      { id: '2', data: { label: 'Commissioning' }, position: { x: 100, y: 130 }, style: { background: '#FFFFFF' } },
      { id: '4', data: { label: 'Aggregation' }, position: { x: 100, y: 200 }, style: { background: '#FFFFFF' } },
      { id: '6', data: { label: 'WMS Inbound' }, position: { x: 300, y: 270 }, style: { background: '#FFFFFF' } },
      { id: '7', data: { label: 'WMS Outbound' }, position: { x: 300, y: 350 }, style: { background: '#FFFFFF' } },
      { id: '8', data: { label: 'stock_inbound(HUB)' }, position: { x: 500, y: 470 }, style: { background: '#FFFFFF' } },
      { id: '9', data: { label: 'stock_outbound(HUB)' }, position: { x: 700, y: 470 }, style: { background: '#FFFFFF' } },
      { id: '10', data: { label: 'stock_inbound(Wholesaler)' }, position: { x: 700, y: 570 }, style: { background: '#FFFFFF' } },
      { id: '11', data: { label: 'stock_outbound(Sell)' }, position: { x: 900, y: 570 }, style: { background: '#FFFFFF' } }
    ]
    : [
      { id: '2', data: { label: 'Custom Inbound' }, position: { x: 100, y: 130 }, style: { background: '#FFFFFF' } },
      { id: '3', data: { label: 'Custom Outbound' }, position: { x: 100, y: 210 }, style: { background: '#FFFFFF' } },
      { id: '9', data: { label: 'stock_inbound(HUB)' }, position: { x: 500, y: 550 }, style: { background: '#FFFFFF' } },
      { id: '10', data: { label: 'stock_outbound(HUB)' }, position: { x: 700, y: 550 }, style: { background: '#FFFFFF' } },
      { id: '11', data: { label: 'stock_inbound(Wholesaler)' }, position: { x: 700, y: 650 }, style: { background: '#FFFFFF' } },
      { id: '12', data: { label: 'stock_outbound(Sell)' }, position: { x: 900, y: 650 }, style: { background: '#FFFFFF' } }
    ];

  // 노드 간 연결 (이벤트 흐름을 표현)
  const edges = nodes
    .map((node, index) => {
      if (index < nodes.length - 1) {
        return {
          id: `e${nodes[index].id}-${nodes[index + 1].id}`,
          source: nodes[index].id,
          target: nodes[index + 1].id,
          animated: true,
          style: { stroke: '#333' }
        };
      }
      return null;
    })
    .filter(Boolean);

  return (
    <div className="flex-1 w-full border border-gray-300 rounded-lg p-2 shadow-md bg-white">
      <h2 className="text-center text-lg font-bold mb-4">
        SCM 과정 흐름 ({isDomestic ? '국내산' : '수입산'})
      </h2>
      <ReactFlow nodes={nodes} edges={edges} fitView>
        <Background />
        <Controls />
      </ReactFlow>
    </div>
  );
}
