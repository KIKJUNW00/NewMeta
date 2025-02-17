import React, { useState, useEffect } from "react";
import axios from "axios";

export default function AllPostsBoard() {
  const [posts, setPosts] = useState([]); // 전체 게시글 상태
  const [selectedPost, setSelectedPost] = useState(null); // 선택한 게시글
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 번호
  const postsPerPage = 7; // 페이지당 게시글 수

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
    <div className="relative overflow-x-auto mt-2 ">
      <table className="w-full min-w-[300px] text-sm text-left  text-gray-500 dark:text-gray-400 ">
        <thead className="text-xs text-gray-700 uppercase  bg-gray-400 dark:bg-gray-700 dark:text-gray-400 ">
          <tr>
            <th className="p-2 border">No</th>
            <th className="p-2 w-[50%] border">제목</th>
            <th className="p-2 border">작성자</th>
            <th className="p-2 border">작성 시간</th>
            <th className="p-2 border">액션</th>
          </tr>
        </thead>
        <tbody>
          {currentPosts.map((post, index) => (
            <tr key={post.id} className="hover:bg-gray-100 bg-white ">
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
        {/* Prev 버튼 */}
        <button
          onClick={() => setCurrentPage(currentPage - 1)}
          disabled={currentPage === 1}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
        >
          Prev
        </button>

        <span className="px-4 py-2">Page {currentPage} of {totalPages}</span>

        {/* Next 버튼 */}
        <button
          onClick={() => setCurrentPage(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
        >
          Next
        </button>
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
