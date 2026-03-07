
const Header = ({ currentUser, isProfileOpen, setIsProfileOpen, setIsLogoutModalOpen }) => {
    return (
        <header className="h-16 border-b flex items-center justify-between px-6 bg-white shrink-0">
            <div className="flex items-center text-sm text-gray-500">
                <span className="text-gray-900 ml-1 font-medium mr-1">Dashboard /</span>Biletlerin Genel Durumu
            </div>

            <div className="relative ml-auto flex items-center space-x-3">

                {/* İsim ve Soyisim Alanı */}
                <div className="text-right hidden sm:block">
                    <p className="mb-1">
                        {currentUser?.name} {currentUser?.surname}
                    </p>

                </div>
                <button onClick={() => setIsProfileOpen(!isProfileOpen)} className="p-1 hover:bg-gray-100 rounded-full transition">
                    <img
                        src={`https://ui-avatars.com/api/?name=${encodeURIComponent(currentUser?.name + " " + currentUser?.surname)}&background=0747A6&color=fff`}
                        alt="Profile"
                        className="w-8 h-8 rounded-full border-2 border-blue-500"
                    />
                </button>
                {isProfileOpen && (
                    <div className="absolute top-full right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-xl py-2 z-[999] animate-in fade-in slide-in-from-top-2 duration-200">
                        <div className="px-4 py-2 border-b text-[10px] text-gray-400 uppercase font-black tracking-widest">
                            Hesap
                        </div>
                        <button className="w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors">
                            Profil Ayarları
                        </button>
                        <button
                            onClick={() => {
                                setIsProfileOpen(false);
                                setIsLogoutModalOpen(true);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 border-t mt-1 transition-colors"
                        >
                            Çıkış Yap
                        </button>
                    </div>
                )}
            </div>
        </header>
    );
};

export default Header;