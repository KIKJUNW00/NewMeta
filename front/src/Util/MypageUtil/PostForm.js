import React, { useState } from "react";
import axios from "axios";

export default function PostForm({ setPosts, posts }) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem("authToken");
    const username = localStorage.getItem("username");

    if (!token || !username) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await axios.post(
        "http://10.125.121.228:8080/community/posts",
        {
          title,
          content,
          admin: { username },
        },
        {
          headers: {
            Authorization: token,
          },
        }
      );

      const newPost = {
        ...response.data,
        username: response.data.username,
      };

      // ✅ 상태 업데이트 및 리 렌더링
      setPosts((prevPosts) => [newPost, ...prevPosts]);
      setTitle("");
      setContent("");

      // ✅ 상태 업데이트 이후 알림 표시 및 새로고침
      if (window.confirm("피드백이 성공적으로 등록되었습니다. 새로고침하시겠습니까?")) {
        window.location.reload();  // 새로고침하여 전체 데이터 반영
      }

    } catch (error) {
      console.error("게시글 등록 중 오류 발생:", error);
      if (error.response) {
        console.error("서버 응답:", error.response.data);
      }
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <h2 className="text-lg font-bold text-gray-700">피드백 작성</h2>
      <div>
        <label className="block text-gray-600">제목</label>
        <input
          type="text"
          className="w-full p-2 border rounded"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
      </div>
      <div>
        <label className="block text-gray-600">내용</label>
        <textarea
          className="w-full p-2 border rounded"
          rows="4"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />
      </div>
      <button
        type="submit"
        className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600"
      >
        피드백 등록
      </button>
    </form>
  );
}
