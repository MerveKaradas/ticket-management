import { useState, useEffect } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Header from "./Header";
import Api from "../../services/Api";
import LogoutConfirmModal from "../modals/LogoutConfirmModal";
import { useNavigate } from "react-router-dom";

const Layout = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [currentUser, setCurrentUser] = useState(null);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    Api.get('/users/getCurrentUser')
      .then(res => setCurrentUser(res.data))
      .catch(err => console.error("Kullanıcı yüklenemedi", err));
  }, []);

  const confirmLogout = async () => {
    try {

      await Api.post('/auth/logout');

    } catch (err) {
      console.error("Logout hatası (yine de çıkış yapılıyor):", err);
    } finally {
      setIsLogoutModalOpen(false);
      localStorage.clear();

      navigate('/home', { replace: true });
    }
  };

  return (

    <div className="flex h-screen w-full overflow-hidden font-sans text-gray-900">

      {/* Sabit Sidebar */}
      <Sidebar isOpen={isOpen} setIsOpen={setIsOpen} currentUser={currentUser}/>

      <div className="relative flex-1 flex flex-col min-w-0 overflow-hidden p-4 space-y-4">
        <Header
          currentUser={currentUser}
          isProfileOpen={isProfileOpen}
          setIsProfileOpen={setIsProfileOpen}
          setIsLogoutModalOpen={setIsLogoutModalOpen}
        />

        {/* İçerik Alanı*/}
        <main className="flex-1 overflow-y-auto min-w-0">
          <Outlet context={{ currentUser }} />
        </main>

        <LogoutConfirmModal
          isOpen={isLogoutModalOpen}
          onClose={() => setIsLogoutModalOpen(false)}
          onConfirm={confirmLogout}
        />
      </div>
    </div>
  );
};

export default Layout;