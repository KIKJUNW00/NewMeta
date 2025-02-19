import React, { useState, useEffect } from "react";
import HubSummaryCard from "./HubSummaryCard";

export default function HubData() {
  const [hubData, setHubData] = useState({});
  const [domesticTotal, setDomesticTotal] = useState(0);
  const [importedTotal, setImportedTotal] = useState(0);
  const [anomalyTotal, setAnomalyTotal] = useState(0);

  useEffect(() => {
    const fetchHubData = async () => {
      try {
        const response = await fetch("http://localhost:8080/scm/hub-wise-data");
        const result = await response.json();
        setHubData(result.hubWiseData || {});
        setDomesticTotal(result.domestic_total || 0);
        setImportedTotal(result.imported_total || 0);
        setAnomalyTotal(result.anomaly_total || 0);
      } catch (error) {
        console.error("Error fetching hub data:", error);
      }
    };
    fetchHubData();
  }, []);

  return (
    <div className=" overflow-auto w-full h-full p-6 flex flex-col ">
      <h2 className="text-lg font-bold text-gray-700 mb-4">허브별 물류 요약</h2>

      {/* 전체 물류량 요약 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-blue-100 p-4 rounded shadow">
          <h3 className="text-blue-800 font-bold text-xl">📦 총 국내 물류량</h3>
          <p className="text-blue-600 text-2xl">{domesticTotal.toLocaleString()} 건</p>
        </div>
        <div className="bg-green-100 p-4 rounded shadow">
          <h3 className="text-green-800 font-bold text-xl">🌐 총 수입 물류량</h3>
          <p className="text-green-600 text-2xl">{importedTotal.toLocaleString()} 건</p>
        </div>
        <div className="bg-red-100 p-4 rounded shadow">
          <h3 className="text-red-800 font-bold text-xl">⚠️ 이상치 총합</h3>
          <p className="text-red-600 text-2xl">{anomalyTotal.toLocaleString()} 건</p>
        </div>
      </div>

      {/* 허브별 물류량 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <HubSummaryCard hubData={hubData} />
      </div>
    </div>
  );
}