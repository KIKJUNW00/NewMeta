import { Route, Routes, BrowserRouter } from 'react-router-dom';
import LoginPage from './Pages/LoginPages';
import AdminPage from './Pages/AdminPage';
import { AuthProvider, useAuth } from "./Util/AuthProvider";





export default function App() {
  return (
    <div className='Main'>
      
      <AuthProvider>

        <BrowserRouter>
          <Routes>

            {/* 로그인 페이지 */}
            <Route path="/" element={<LoginPage />} />
            {/* 관리자 페이지 */}
            <Route path="AdminPage/*" element={<AdminPage />} />
            {/* 컨텐츠 페이지 */}


          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </div>
  );

};



