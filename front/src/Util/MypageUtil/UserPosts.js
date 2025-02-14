import React, { useState, useEffect } from "react";
import axios from "axios";

export default function UserPosts({ setPosts }) {
  const [posts, setPostsState] = useState([]);
  const [selectedPost, setSelectedPost] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  const username = localStorage.getItem("username");
  const token = localStorage.getItem("authToken");

  const fetchMyPosts = async () => {
    try {
      const response = await axios.get("http://10.125.121.228:8080/community/my-posts", {
        headers: {
          Authorization: token,
        },
      });
      setPostsState(response.data);
      setPosts(response.data);
    } catch (error) {
      console.error("내 게시글 목록 조회 오류:", error);
    }
  };

  useEffect(() => {
    fetchMyPosts();
  }, []);

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://10.125.121.228:8080/community/posts/${id}`, {
        headers: {
          Authorization: token,
        },
      });
      setPostsState((prevPosts) => prevPosts.filter((post) => post.id !== id));
      setSelectedPost(null);
      alert("게시글이 삭제되었습니다.");
    } catch (error) {
      console.error("게시글 삭제 오류:", error);
    }
  };

  const handleUpdate = async () => {
    if (!selectedPost) return;

    try {
      await axios.put(
        `http://10.125.121.228:8080/community/posts/${selectedPost.id}`,
        {
          title: selectedPost.title,
          content: selectedPost.content,
          admin: { username },
        },
        {
          headers: {
            Authorization: token,
          },
        }
      );
      setPostsState((prevPosts) =>
        prevPosts.map((post) => (post.id === selectedPost.id ? selectedPost : post))
      );
      setIsEditing(false);
      alert("게시글이 수정되었습니다.");
      window.location.reload();  // ✅ 수정 완료 후 페이지 새로고침
    } catch (error) {
      console.error("게시글 수정 오류:", error);
    }
  };

  const handleViewDetails = (post) => {
    setSelectedPost(post);
    setIsEditing(false);
  };

  return (
    <div className="mt-8">
      <h2 className="text-lg font-bold text-gray-700 text-center">내 게시글 목록</h2>
      <table className="w-full mt-4 border-collapse border border-gray-300 text-center">
        <thead className="bg-gray-100">
          <tr>
            <th className="p-2 border">No</th>
            <th className="p-2 border">제목</th>
            <th className="p-2 border">작성 시간</th>
            <th className="p-2 border">수정 시간</th>
            <th className="p-2 border">액션</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post, index) => (
            <tr key={post.id} className="hover:bg-gray-100">
              <td className="p-2 border">{index + 1}</td>
              <td className="p-2 border">{post.title}</td>
              <td className="p-2 border">{new Date(post.createdAt).toLocaleString()}</td>
              <td className="p-2 border">
                {post.updatedAt && post.updatedAt !== post.createdAt
                  ? new Date(post.updatedAt).toLocaleString()
                  : "-"}
              </td>
              <td className="p-2 border">
                <button
                  onClick={() => handleViewDetails(post)}
                  className="text-blue-500 hover:underline mr-2"
                >
                  상세보기
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 상세보기/수정 모달 */}
      {selectedPost && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow-lg w-1/2">
            {isEditing ? (
              <>
                <h3 className="text-lg font-bold mb-2">게시글 수정</h3>
                <div className="mb-4">
                  <label className="block text-gray-600">제목</label>
                  <input
                    type="text"
                    className="w-full p-2 border rounded"
                    value={selectedPost.title}
                    onChange={(e) => setSelectedPost({ ...selectedPost, title: e.target.value })}
                  />
                </div>
                <div className="mb-4">
                  <label className="block text-gray-600">내용</label>
                  <textarea
                    className="w-full p-2 border rounded"
                    rows="4"
                    value={selectedPost.content}
                    onChange={(e) => setSelectedPost({ ...selectedPost, content: e.target.value })}
                  />
                </div>
                <div className="mt-4 text-right">
                  <button
                    onClick={() => setIsEditing(false)}
                    className="bg-gray-500 text-white py-2 px-4 rounded mr-2"
                  >
                    취소
                  </button>
                  <button
                    onClick={handleUpdate}
                    className="bg-blue-500 text-white py-2 px-4 rounded"
                  >
                    수정 완료
                  </button>
                </div>
              </>
            ) : (
              <>
                <h3 className="text-lg font-bold mb-2">{selectedPost.title}</h3>
                <p className="text-gray-600">{selectedPost.content}</p>
                <div className="mt-4 text-right">
                  <button
                    onClick={() => setIsEditing(true)}
                    className="bg-green-500 text-white py-2 px-4 rounded mr-2"
                  >
                    수정
                  </button>
                  <button
                    onClick={() => handleDelete(selectedPost.id)}
                    className="bg-red-500 text-white py-2 px-4 rounded mr-2"
                  >
                    삭제
                  </button>
                  <button
                    onClick={() => setSelectedPost(null)}
                    className="bg-gray-500 text-white py-2 px-4 rounded"
                  >
                    닫기
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
