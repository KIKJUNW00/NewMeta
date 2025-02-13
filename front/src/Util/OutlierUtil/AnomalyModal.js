import React from "react";

export default function AnomalyModal({ productData, selectedAnomalies, setSelectedAnomalies, handleDeleteSelected, toggleModal }) {
  const handleCheckboxChange = (id) => {
    setSelectedAnomalies((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  return (
    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white p-6 rounded-md w-1/2">
        <h2 className="text-xl font-bold mb-4">상세보기 및 삭제</h2>
        <div className="overflow-y-auto max-h-60">
          <table className="w-full text-sm text-left text-gray-700 border-collapse">
            <thead className="bg-gray-100 text-gray-700 uppercase text-xs">
              <tr>
                <th className="px-6 py-3">Select</th>
                <th className="px-6 py-3">Product Name</th>
                <th className="px-6 py-3">Anomaly Type</th>
              </tr>
            </thead>
            <tbody>
              {productData.map((item) => (
                <tr key={item.id} className="bg-white border-b hover:bg-gray-100">
                  <td className="px-6 py-4">
                    <input
                      type="checkbox"
                      checked={selectedAnomalies.includes(item.anomalyId)}
                      onChange={() => handleCheckboxChange(item.anomalyId)}
                    />
                  </td>
                  <td className="px-6 py-4">{item.anomalyProductName}</td>
                  <td className="px-6 py-4">{item.anomalyType}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="flex justify-end mt-4">
          <button onClick={handleDeleteSelected} className="px-4 py-2 bg-red-500 text-white rounded">
            삭제
          </button>
          <button onClick={toggleModal} className="ml-2 px-4 py-2 bg-gray-300 rounded">
            닫기
          </button>
        </div>
      </div>
    </div>
  );
}
