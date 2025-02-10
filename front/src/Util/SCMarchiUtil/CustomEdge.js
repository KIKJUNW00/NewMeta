import React from 'react';

export const CustomEdge = ({
  id,
  sourceX,
  sourceY,
  targetX,
  targetY,
  markerEnd,
}) => {
  const edgePath = `M${sourceX},${sourceY} C${sourceX},${(sourceY + targetY) / 2} ${targetX},${(sourceY + targetY) / 2} ${targetX},${targetY}`;

  return (
    <g>
      <path
        id={id}
        className="react-flow__edge-path"
        d={edgePath}
        style={{
          stroke: '#FF0072',
          strokeWidth: 2,
          fill: 'none',
          animation: 'dash-animation 2s infinite linear',
        }}
        markerEnd={markerEnd}
      />
      <style>
        {`
          @keyframes dash-animation {
            to {
              stroke-dashoffset: -1000;
            }
          }
        `}
      </style>
    </g>
  );
};
