import { Link, useLocation } from "react-router-dom"; // Link ve aktif sayfayı anlamak için useLocation

const Sidebar = ({ isOpen, setIsOpen }) => {
  const location = useLocation(); // Mevcut sayfa yolunu alır

  const menuItems = [
    { name: 'Dashboard', path: '/', icon: '📊' },
    { name: 'Board', path: '/board', icon: '📋' },
    { name: 'Reports', path: '/Reports', icon: '📈' },
    { name: 'Settings', path: '/Settings', icon: '⚙️' }
  ];

  return (
    <aside className={`${isOpen ? 'w-64' : 'w-20'} bg-[#0747A6] text-white transition-all duration-300 flex flex-col relative shrink-0`}>
      <div className="p-4 flex items-center space-x-3">
        <div className="min-w-[40px] h-10 bg-white/20 rounded flex items-center justify-center font-bold text-xl">T</div>
        {isOpen && <span className="font-bold text-lg whitespace-nowrap">Ticket Pro</span>}
      </div>

      <nav className="flex-1 mt-6 px-3 space-y-1">
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;

          return (
            <Link
              title={item.name}
              key={item.name}
              to={item.path}
              className={`flex items-center p-3 rounded cursor-pointer transition-colors group ${isActive ? 'bg-white/20' : 'hover:bg-white/10'
                }`}
            >
              <span className="text-xl opacity-70 group-hover:opacity-100">{item.icon}</span>
              {isOpen && <span className="ml-4 text-sm font-medium">{item.name}</span>}
            </Link>
          );
        })}
      </nav>

      {/* Açma/Kapama Butonu */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="absolute -right-3 top-20 bg-white text-blue-800 rounded-full w-6 h-6 border shadow-md flex items-center justify-center text-[10px] z-20 hover:scale-110 transition-transform"
      >
        {isOpen ? '❮' : '❯'}
      </button>
    </aside>
  );
};

export default Sidebar;