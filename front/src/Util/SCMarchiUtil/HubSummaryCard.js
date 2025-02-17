import React from "react";

export default function HubSummaryCard({ hubData }) {
  return (
    <>
      {Object.entries(hubData).map(([hubName, totalCount], index) => (
        <div key={index} className="bg-white p-4 rounded shadow">
          <h3 className="text-gray-800 font-bold text-lg">{hubName.replace(/_/g, " ")}</h3>
          <p className="text-gray-600 text-md mt-2"> 총 물류량: <strong>{totalCount.toLocaleString()} 건</strong></p>
        </div>
      ))}
    </>
  );
}
