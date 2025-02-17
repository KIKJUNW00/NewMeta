import React, { useState, useEffect } from "react";

export default function ProfileSection({ userData }) {
  const { role, photo } = userData || {}; // userData가 undefined일 경우 방지
  const token = localStorage.getItem("authToken");
  const username = localStorage.getItem("username");

  const [profilePhoto, setProfilePhoto] = useState(
    userData.photo ? `http://10.125.121.228:8080${userData.photo}` : "https://via.placeholder.com/100"
  );
  
  const [selectedFile, setSelectedFile] = useState(null);

  // ✅ 새로고침 시 최신 프로필 정보 가져오기
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch(`http://10.125.121.228:8080/admin/${username}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (response.ok) {
          const userData = await response.json();
          console.log("사용자 정보 불러오기 성공:", userData);

          // 최신 프로필 사진 적용
          if (userData.photo) {
            setProfilePhoto(`http://10.125.121.228:8080${userData.photo}`);
          }
        } else {
          console.error("사용자 정보 불러오기 실패");
        }
      } catch (error) {
        console.error("네트워크 오류:", error);
      }
    };

    if (username && token) {
      fetchUserData();
    }
  }, [username, token]);
  

  if (!token || !username) {
    alert("로그인이 필요합니다.");
    return null;
  }

  // 파일 선택 핸들러
  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      setProfilePhoto(imageUrl);
      setSelectedFile(file);
    }
  };

  // 프로필 저장 핸들러 (API 연동 가능)
  const handleSaveProfile = async () => {
    if (!selectedFile) {
      alert("변경할 프로필 사진을 선택하세요.");
      return;
    }
  
    const token = localStorage.getItem("authToken");
    if (!token) {
      alert("로그인이 필요합니다. 다시 로그인해주세요.");
      return;
    }
  
    try {
      // 1. 이미지 파일 업로드
      const formData = new FormData();
      formData.append("file", selectedFile);
  
      console.log("이미지 업로드 중...");
  
      const uploadResponse = await fetch(`http://10.125.121.228:8080/admin/${username}/upload-photo`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });
  
      const uploadData = await uploadResponse.json();
      console.log("업로드 응답:", uploadData);
  
      if (!uploadResponse.ok || !uploadData.photo) {
        alert("이미지 업로드 실패!");
        return;
      }
  
      // **절대 경로로 변환**
      const imageUrl = `http://10.125.121.228:8080${uploadData.photo}`;
      console.log("업로드된 이미지 URL:", imageUrl);
  
      setProfilePhoto(imageUrl); // UI에서 프로필 사진 갱신
  
      // 2. 사용자 정보 업데이트 요청
      console.log(`Updating profile at: http://10.125.121.228:8080/admin/${username}`);
  
      const updateResponse = await fetch(`http://10.125.121.228:8080/admin/${username}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: username,
          role: role,
          photo: uploadData.photo, // 백엔드 저장용 상대 경로
        }),
      });
  
      const updateData = await updateResponse.json();
      console.log("프로필 업데이트 응답:", updateData);
  
      if (updateResponse.ok) {
        alert("프로필이 성공적으로 업데이트되었습니다!");
  
        // **최종 업데이트된 프로필 이미지 적용**
        const updatedImageUrl = `http://10.125.121.228:8080${updateData.photo}`;
        console.log("최종 반영된 이미지 URL:", updatedImageUrl);
  
        setProfilePhoto(updatedImageUrl); // UI 업데이트
        setSelectedFile(null); // 저장 버튼 숨기기
      } else {
        console.error("프로필 업데이트 실패:", updateData);
        alert(`프로필 업데이트 실패: ${updateData.message || "알 수 없는 오류"}`);
      }
    } catch (error) {
      console.error("네트워크 오류:", error);
      alert("서버와 통신 중 오류가 발생했습니다.");
    }
  };
  

  return (
    <div className="flex space-x-6 m-2 p-2 ">

      <div className="flex flex-col justify-center items-center">

        {/* 프로필 이미지 */}
        <img
          src={profilePhoto}
          alt="Profile"
          className="w-24 h-24 rounded-full object-cover border border-gray-300"
        />
        {/* 파일 업로드 */}
        <input
          type="file"
          accept="image/*"
          onChange={handleFileChange}
          className="hidden"
          id="fileInput"
        />
        <label
          htmlFor="fileInput"
          className="px-2 py-1 mt-2 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-600"
        >
          프로필 사진 변경
        </label>
      </div>

      {/* 저장 버튼 */}
      {selectedFile && (
        <button
          onClick={handleSaveProfile}
          className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
        >
          프로필 저장
        </button>
      )}

      {/* 사용자 정보 */}
      <div className="items-start justify-center pt-5">
        <h2 className="text-xl font-bold text-gray-700">{username}</h2>
        <p className="text-gray-500">{role}</p>
      </div>
    </div>
  );
}
