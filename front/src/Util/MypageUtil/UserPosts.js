import React, { useState } from "react";
import axios from "axios";

export default function UserPosts({ username, posts, setPosts }) {
  const [selectedPost, setSelectedPost] = useState(null);

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://10.125.121.228:8080/community/posts/${id}`);
      setPosts((prevPosts) => prevPosts.filter((post) => post.id !== id));
      setSelectedPost(null); // 모달창 닫기
      alert("게시글이 삭제되었습니다.");
    } catch (error) {
      console.error("게시글 삭제 오류:", error);
    }
  };

  const handleViewDetails = (post) => {
    setSelectedPost(post);
  };

  return (
    <div className="mt-8">
      <h2 className="text-lg font-bold text-gray-700">내 게시글 목록</h2>
      <table className="w-full mt-4 border-collapse border border-gray-300">
        <thead className="bg-gray-100">
          <tr>
            <th className="p-2 border">No</th>
            <th className="p-2 border">제목</th>
            <th className="p-2 border">작성 시간</th>
            <th className="p-2 border">액션</th>
          </tr>
        </thead>
        <tbody>
          {posts
            .filter((post) => post.admin.username === username) // **현재 로그인된 유저의 글만 필터링**
            .map((post, index) => (
              <tr key={post.id} className="hover:bg-gray-100">
                <td className="p-2 border text-center">{index + 1}</td>
                <td className="p-2 border">{post.title}</td>
                <td className="p-2 border">{new Date(post.createdAt).toLocaleString()}</td>
                <td className="p-2 border text-center">
                  <button
                    onClick={() => handleViewDetails(post)}
                    className="text-blue-500 hover:underline mr-2"
                  >
                    상세보기
                  </button>
                  <button
                    onClick={() => handleDelete(post.id)}
                    className="text-red-500 hover:underline"
                  >
                    삭제
                  </button>
                </td>
              </tr>
            ))}
        </tbody>
      </table>

      {/* 상세보기 모달 */}
      {selectedPost && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow-lg w-1/2">
            <h3 className="text-lg font-bold mb-2">{selectedPost.title}</h3>
            <p className="text-gray-600">{selectedPost.content}</p>
            <div className="mt-4 text-right">
              <button
                onClick={() => setSelectedPost(null)}
                className="bg-gray-500 text-white py-2 px-4 rounded mr-2"
              >
                닫기
              </button>
              <button
                onClick={() => handleDelete(selectedPost.id)}
                className="bg-red-500 text-white py-2 px-4 rounded"
              >
                삭제
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
