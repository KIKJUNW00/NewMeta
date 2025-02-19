import React, { useState, useEffect } from "react";
import axios from "axios";
import ProfileSection from "../Util/MypageUtil/ProfileSection";
import PostForm from "../Util/MypageUtil/PostForm";
import UserPosts from "../Util/MypageUtil/UserPosts";

export default function MyPage() {
  const [userData, setUserData] = useState(null);
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await axios.get("http://localhost:8080/admin");
        const loggedInUser = response.data.find(user => user.username); // 예시로 admin 사용
        setUserData(loggedInUser);
      } catch (error) {
        console.error("사용자 데이터 가져오기 오류:", error);
      }
    };

    const fetchPosts = async () => {
      try {
        const response = await axios.get("http://localhost:8080/community/all-posts");
        const filteredPosts = response.data.map((post) => ({
          ...post,
          admin: { username: post.admin.username },  // admin 객체에서 username만 남김
        }));
        setPosts(filteredPosts);
      } catch (error) {
        console.error("게시글 가져오기 오류:", error);
      }
    };


    fetchUserData();
    fetchPosts();
  }, []);

  return (
    <div className="h-screen overflow-auto bg-gray-100 p-4 flex items-center justify-center">
      <div className="bg-white w-full h-full overflow-auto shadow-lg p-4 flex flex-col space-y-4">
        <div className="border border-gray-300">
          {/* 1. 프로필 섹션 */}
          {userData && <ProfileSection userData={userData} />}
        </div>

        <div className="border border-gray-300 p-5">
          {/* 2. 게시글 작성 섹션 */}
          {userData && <PostForm username={userData.username} setPosts={setPosts} posts={posts} />}
        </div>

        <div className="border border-gray-300 p-5">
          {/* 3. 사용자 게시글 목록 섹션 */}
          <UserPosts username={userData?.username} posts={posts} setPosts={setPosts} />
        </div>

      </div>
    </div>
  );
}
