import React from "react";

export default function ProfileSection({ userData }) {
  const { username, role, photo } = userData;

  return (
    <div className="flex items-center space-x-6">
      <img
        src={photo || "https://via.placeholder.com/100"} // 기본 이미지 또는 사용자 사진
        alt="Profile"
        className="w-24 h-24 rounded-full object-cover border border-gray-300"
      />
      <div>
        <h2 className="text-xl font-bold text-gray-700">{username}</h2>
        <p className="text-gray-500">{role}</p>
      </div>
    </div>
  );
}
