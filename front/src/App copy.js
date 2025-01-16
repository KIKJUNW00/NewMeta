import React, { useState, useEffect } from 'react';
import mainLogo from './img/mainLogo1.png';
import reallogo from './img/reallogo1.png';

function App() {
  const [showMainLogo, setShowMainLogo] = useState(true);
  // const [showRealLogo, setShowRealLogo] = useState(false);
  const [showLogin, setShowLogin] = useState(false);

  useEffect(() => {
    const timer1 = setTimeout(() => {
      setShowMainLogo(false);
      // setShowRealLogo(true);
    }, 3000); // 3.0초 후 mainLogo가 사라짐
    
  
    const timer2 = setTimeout(() => {
      setShowLogin(true);
    }, 3000); // 추가 3.0초 후 로그인 화면 표시

    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
  }, []);

  return (
    <div className="h-screen bg-gradient-to-b
     from-[#3182D0] via-[#3180CE] to-[#19426A] 
     flex items-center justify-center">
      <div className="text-center">
        {/* mainLogo 애니메이션 */}
        {showMainLogo &&(
          <img
            src={mainLogo}
            alt="Main company logo"
            className="animate-faderight"
          />
        )}

        {/* 로그인 화면 */}
        {showLogin && (
          <div className="animate-slideup bg-opacity-40
                        bg-white py-11 rounded-lg w-">
           
              <img
                src={reallogo}
                alt="Login company logo"
                className="mb-12 mx-0"
              />
      

            <h2 className="text-2xl text-slate-950 font-bold mb-4">로그인</h2>
            <form>
              <input
                type="text"
                placeholder="아이디"
                className="w-3/4 p-2 mb-4 border border-gray-300 rounded-lg"
              />
              <input
                type="password"
                placeholder="비밀번호"
                className="w-3/4 p-2 mb-4 border border-gray-300 rounded-lg"
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

export default App;
