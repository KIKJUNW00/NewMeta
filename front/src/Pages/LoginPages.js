import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import mainLogo from '../img/mainLogo1.png';
import reallogo from '../img/reallogo1.png';
import { useAuth } from '../Util/AuthProvider'

export default function Login() {


  // ------------------------------------------------------------------------
  const [errorMessage, setErrorMessage] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();
  const [user, setUser] = useState({
    username: '',
    password: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser({ ...user, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // 로그인 데이터 설정(JSON형식)
      const loginData = {
        username: user.username,
        password: user.password

      };

      // axios.post는 첫 번째 인자로 URL, 두 번째 인자로 데이터를 받습니다.
      const resp = await axios.post(
        'http://10.125.121.228:8080/login', //url
        loginData, // JSON 데이터로 보내기 //요청데이터
        {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json'
          }
        } //옵션
      );
      console.log('로그인 요청 성공:', resp); // 전체 응답 출력

      if (resp.status === 200) {
        alert('로그인 성공!');
        console.log('로그인 성공:', resp.data || '응답 데이터가 비어 있습니다.');

        // 전역 상태에 로그인 반영
        login(resp.headers.get('Authorization')); // 전역 상태에 토큰 저장

        // JWT 토큰을 서버에서 응답으로 받았다고 가정
        const jwtToken = resp.headers.get('Authorization');  // 서버에서 JWT 토큰을 받은 경우
        // JWT 토큰을 로컬 스토리지에 저장
        localStorage.setItem('authToken', jwtToken);


        // 토큰이 성공적으로 저장되었음을 콘솔에 출력
        // console.log("JWT 토큰:", jwtToken);
        // console.log("유저이름: ", user.username);
        navigate('/AdminPage/DashBoard', { state: { userData: { username: user.username } } });
      }
    } catch (error) {
      if (error.response) {
        console.error('Response Error:', error.response.data, errorMessage);
        setErrorMessage('로그인 정보가 일치하지 않습니다');
      } else {
        console.error('Network Error:', error.message);
        setErrorMessage('서버와의 연결에 문제가 발생했습니다.');
      }
    }
  };


  // ------------------------------------------------------------------------
  // {웹사이트 입장시 뜨는 로고와 로그인화면 애니메이션 시간}

  const [showMainLogo, setShowMainLogo] = useState(true);
  const [showLogin, setShowLogin] = useState(false);

  useEffect(() => {
    const timer1 = setTimeout(() => {
      setShowMainLogo(false);
    }, 3000); // 3.0초 후 mainLogo가 사라짐


    const timer2 = setTimeout(() => {
      setShowLogin(true);
    }, 3000); // 추가 3.0초 후 로그인 화면 표시

    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
    // ------------------------------------------------------------------------
  }, []);

  return (
    <div className="h-screen bg-gradient-to-b
     from-[#4C585B] via-[#778892] to-[#2E3A3E]
     flex items-center justify-center">
      <div className="text-center">
        {/* mainLogo 애니메이션 */}
        {showMainLogo && (
          <img
            src={mainLogo}
            alt="Main company logo"
            className="animate-faderight"
          />
        )}

        {/* 로그인 화면 */}
        {showLogin && (
          <div className="animate-slideup bg-opacity-60
                        bg-white py-11 rounded-lg">

            <img
              src={reallogo}
              alt="Login company logo"
              className="mb-12 mx-0"
            />


            <h2 className="text-2xl text-slate-950 font-bold mb-4">로그인</h2>
            <form onSubmit={handleSubmit}>
              <input
                type="text"
                placeholder="아이디"
                className="w-3/4 p-2 mb-4 border border-gray-300 rounded-lg"
                name='username'
                value={user.username}
                onChange={handleChange}
              />
              <input
                type="password"
                placeholder="비밀번호"
                className="w-3/4 p-2 mb-4 border border-gray-300 rounded-lg"
                name='password'
                value={user.password}
                onChange={handleChange}
              />
              <p className='mb-10 text-left ml-16 text-red-500 '>비밀번호를 입력하세요</p>
              <button
                type="submit"
                className="bg-blue-500 text-white
                px-4 py-2 rounded-lg w-3/4"
              >
                로그인
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}

