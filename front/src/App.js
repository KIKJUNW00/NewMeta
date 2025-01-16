import { Route, Routes, BrowserRouter, Link } from 'react-router-dom';
import LoginPage from './Pages/LoginPages';
import AdminPage from './Pages/AdminPage';


export default function App() {
  return (
    <div className='Main'>
      <BrowserRouter>
        <Routes>
            
            {/* 로그인 페이지 */}
            <Route path="/" element={<LoginPage />} />

            {/* 관리자 페이지 */}
            <Route path = "/AdminPage" element={<AdminPage />} />
            

        </Routes>
      </BrowserRouter>
    </div>
  );
  
};



