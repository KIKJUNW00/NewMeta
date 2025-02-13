import React, { useState, useEffect } from "react";
import axios from "axios";

export default function PostForm({ username, setPosts, posts }) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const response = await axios.post("http://10.125.121.228:8080/community/posts", {
        title,
        content,
        admin: { username },  // username만 전달
      });

      const newPost = {
        ...response.data,
        admin: response.data.admin.username,  // 응답에서 admin 객체를 admin.username만 유지
      };

      setPosts([newPost, ...posts]);  // 새 게시글을 기존 게시글 목록에 추가
      setTitle("");
      setContent("");
      alert("피드백이 성공적으로 등록되었습니다.");
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
