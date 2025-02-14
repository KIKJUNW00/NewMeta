import React, { useState, useEffect } from "react";
import axios from "axios";

export default function AllPostsBoard() {
  const [posts, setPosts] = useState([]); // 전체 게시글 상태
  const [selectedPost, setSelectedPost] = useState(null); // 선택한 게시글
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 번호
  const postsPerPage = 5; // 페이지당 게시글 수

  // 로컬 스토리지에서 토큰 가져오기
  const token = localStorage.getItem("authToken");

  // 전체 게시글 가져오기
  const fetchAllPosts = async () => {
    try {
      const response = await axios.get("http://10.125.121.228:8080/community/all-posts", {
        headers: {
          Authorization: token,
        },
      });
      setPosts(response.data);
    } catch (error) {
      console.error("전체 게시글 조회 오류:", error);
    }
  };

  useEffect(() => {
    fetchAllPosts();
  }, []);

  // 현재 페이지에 표시할 게시글 계산
  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);
  const totalPages = Math.ceil(posts.length / postsPerPage);

  return (
    <div className="mt-8">
      <h2 className="text-lg font-bold text-gray-700 text-center">전체 게시글 목록</h2>
      <table className="w-full mt-4 border-collapse border border-gray-300 text-center">
        <thead className="bg-gray-100">
          <tr>
            <th className="p-2 border">No</th>
            <th className="p-2 border">제목</th>
            <th className="p-2 border">작성자</th>
            <th className="p-2 border">작성 시간</th>
            <th className="p-2 border">액션</th>
          </tr>
        </thead>
        <tbody>
          {currentPosts.map((post, index) => (
            <tr key={post.id} className="hover:bg-gray-100">
              <td className="p-2 border">{indexOfFirstPost + index + 1}</td>
              <td className="p-2 border">{post.title}</td>
              <td className="p-2 border">{post.username}</td>
              <td className="p-2 border">{new Date(post.createdAt).toLocaleString()}</td>
              <td className="p-2 border">
                <button
                  onClick={() => setSelectedPost(post)}
                  className="text-blue-500 hover:underline"
                >
                  상세보기
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <div className="flex justify-center items-center mt-4 space-x-2">
        {currentPage > 1 && (
          <button
            onClick={() => setCurrentPage(currentPage - 1)}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
          >
            이전
          </button>
        )}
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i + 1}
            onClick={() => setCurrentPage(i + 1)}
            className={`px-4 py-2 rounded ${
              currentPage === i + 1 ? "bg-blue-500 text-white" : "bg-gray-300 hover:bg-gray-400"
            }`}
          >
            {i + 1}
          </button>
        ))}
        {currentPage < totalPages && (
          <button
            onClick={() => setCurrentPage(currentPage + 1)}
            className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
          >
            다음
          </button>
        )}
      </div>

      {/* 상세보기 모달 */}
      {selectedPost && (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[9999]">
    <div className="bg-white p-8 rounded-lg shadow-2xl w-[40%] max-h-[70vh] overflow-y-auto">
      <h3 className="text-2xl font-bold mb-4 text-center">{selectedPost.title}</h3>
      <p className="text-lg text-gray-700 mb-6">{selectedPost.content}</p>
      <p className="text-sm text-gray-500 text-right mb-4">
        작성자: <span className="font-semibold">{selectedPost.username}</span> | 작성 시간:{" "}
        {new Date(selectedPost.createdAt).toLocaleString()}
      </p>
      <div className="mt-4 text-right">
        <button
          onClick={() => setSelectedPost(null)}
          className="bg-gray-600 hover:bg-gray-700 text-white py-2 px-6 rounded-lg transition"
        >
          닫기
        </button>
      </div>
    </div>
  </div>
)}
    </div>
  );
}
