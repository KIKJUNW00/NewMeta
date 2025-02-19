import React, { useState, useEffect, useContext } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import AnomalyModal from "./AnomalyModal";
import { AnomalyContext } from "./AnomalyContext";

export default function OutlierBoard() {
  //  상태 변수 선언: productData는 REST API와 STOMP(WebSocket)를 통해 받아온 이상치 데이터, currentPage는 현재 페이지 번호
  const [productData, setProductData] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAnomalies, setSelectedAnomalies] = useState([]);
  const { setNewAnomaly } = useContext(AnomalyContext);
  const ITEMS_PER_PAGE = 5;

  //  컴포넌트 마운트 시 REST API를 호출하여 초기 이상치 데이터를 가져옴
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("http://localhost:8080/anomalies/paged");
        const data = await response.json();
        console.log("Fetched data:", data); // 초기 데이터 확인용 로그

        // API 응답에서 data.content가 배열인지 확인 후 productData에 저장
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

  // SockJS와 STOMP를 이용한 WebSocket 연결을 통한 실시간 이상 탐지 데이터 수신
  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws-stomp");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("STOMP WebSocket 연결 성공");
        stompClient.subscribe("/topic/anomalyAlerts", (message) => {
          console.log("STOMP 메시지 수신:", message.body);
          try {
            const newEvent = JSON.parse(message.body);
            if (Array.isArray(newEvent) && newEvent.length > 0) {
              setProductData((prevData) => [newEvent[0], ...prevData]);
  
              // 상태 업데이트 (전역 상태 + localStorage)
              setNewAnomaly(true);
              localStorage.setItem("newAnomaly", JSON.stringify(true));
            }
          } catch (err) {
            console.error("STOMP 메시지 파싱 오류:", err);
          }
        });
      },
      onStompError: (frame) => {
        console.error("STOMP 오류 발생:", frame);
      },
    });
  
    stompClient.activate();
  
    return () => stompClient.deactivate();
  }, [setNewAnomaly]);
  



  //  전체 페이지 수 계산
  const totalPages = Math.ceil((productData.length || 1) / ITEMS_PER_PAGE);

  //  페이지 변경 함수: 유효한 페이지 번호일 경우 currentPage 상태 업데이트
  const handlePageChange = (newPage) => {
    if (newPage > 0 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  const toggleModal = () => setIsModalOpen(!isModalOpen); //모달창 오픈

  // 이상치 삭제 핸들러
  const handleDeleteSelected = async () => {
    try {
      // DELETE 요청을 병렬로 실행
      await Promise.all(
        selectedAnomalies.map((id) =>
          fetch(`http://10.125.121.228:8080/anomalies/${id}`, {
            method: "DELETE",
          })
        )
      );

      // 삭제된 항목을 제외하고 상태 업데이트
      setProductData((prevData) => prevData.filter((item) => !selectedAnomalies.includes(item.anomalyId)));

      // 선택 항목 초기화
      setSelectedAnomalies([]);
      toggleModal();
      alert("선택한 이상치가 삭제되었습니다.");
    } catch (error) {
      console.error("Error deleting anomalies:", error);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };


  //  CSV 다운로드 핸들러 (BOM 추가하여 한글 인코딩 문제 해결)
  const handleDownloadCSV = () => {
    // CSV 헤더 행 정의 (표시될 컬럼명)
    const headers = ["Product epcCode", "Anomaly Type", "Reason", "Timestamp", "Hub"];

    // productData 배열을 순회하며 CSV 각 행 데이터 생성
    const rows = productData.map((item) => [
      item.epcCode,
      item.anomalyType,
      item.reason,
      item.anomalyTimestamp,
      item.anomalyHub,
    ]);

    // BOM ("\uFEFF") 추가하여 UTF-8 인코딩 시 한글이 깨지지 않도록 함
    const csvContent =
      "\uFEFF" +
      [headers.join(","), ...rows.map((row) => row.map((value) => `"${value}"`).join(","))].join("\n");

    // CSV 문자열을 포함하는 Blob 생성 (MIME type: text/csv)
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    // Blob URL 생성
    const url = URL.createObjectURL(blob);
    // 다운로드를 위한 임시 a 태그 생성 후 클릭 이벤트 트리거
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "anomalies.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    // 생성된 URL 해제
    URL.revokeObjectURL(url);
  };

  //  현재 페이지에 표시할 데이터 계산 (슬라이스)
  const displayedData = productData.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE
  );

  return (
    <>
      {/* 표를 감싸는 컨테이너 (relative 속성 포함) */}
      <div className="relative overflow-x-auto mt-4 border border-gray-300 rounded-md shadow-sm min-h-[407px] max-h-[410px] overflow-y-auto">
        <table className="w-full text-sm text-left text-gray-700 border-collapse">
          <thead className="bg-gray-100 text-gray-700 uppercase text-xs">
            <tr>
              <th className="px-6 py-3 w-1/5">Product epcCode</th>
              <th className="px-6 py-3 w-1/5">Anomaly Type</th>
              <th className="px-6 py-3 w-[30%]">Reason</th>
              <th className="px-6 py-3 w-1/5">Timestamp</th>
              <th className="px-6 py-3 w-1/5">Hub</th>
            </tr>
          </thead>
          <tbody>
            {displayedData.length > 0 ? (
              displayedData.map((item, index) => (
                <tr key={index} className="bg-white border-b hover:bg-gray-100">
                  <td className="px-6 py-4">{item.epcCode}</td>
                  <td className="px-6 py-4">{item.anomalyType}</td>
                  <td className="px-6 py-4">{item.reason}</td>
                  <td className="px-6 py-4">{item.anomalyTimestamp}</td>
                  <td className="px-6 py-4">{item.anomalyHub}</td>

                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className="px-6 py-4 text-center text-gray-500">
                  No data available
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <AnomalyModal
          productData={productData}
          selectedAnomalies={selectedAnomalies}
          setSelectedAnomalies={setSelectedAnomalies}
          handleDeleteSelected={handleDeleteSelected}
          toggleModal={toggleModal}
        />
      )}

      {/*  CSV 다운로드 버튼과 페이지 네비게이션 버튼들을 같은 행에 배치 */}
      <div className="grid grid-cols-3 items-center mt-4">
        {/* 왼쪽 열: CSV 다운로드 버튼 */}
        <div>
          <button
            onClick={handleDownloadCSV}
            className="px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded"
          >
            CSV 다운로드
          </button>
        </div>
        {/* 가운데 열: 페이지 네비게이션 버튼들을 flex 컨테이너로 가운데 정렬 */}
        <div className="flex justify-center items-center space-x-2">
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50 rounded"
          >
            Prev
          </button>
          <span className="px-4 py-2 text-sm">
            Page {currentPage} of {totalPages}
          </span>
          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50 rounded"
          >
            Next
          </button>
        </div>
        {/* 오른쪽 열: 상세보기 버튼*/}
        <div className="flex justify-end">
          <button
            onClick={toggleModal}
            className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded">
            상세보기
          </button>
        </div>
      </div>
    </>
  );
}
