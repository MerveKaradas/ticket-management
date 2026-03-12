import { Link, useLocation } from "react-router-dom";

const Sidebar = ({ isOpen, setIsOpen, currentUser }) => { 
  const location = useLocation();

  // Herkesin göreceği menüler
  const menuItems = [
    { name: 'Dashboard', path: '/', icon: '📊' },
    { name: 'Board', path: '/board', icon: '📋' },
    { name: 'Reports', path: '/Reports', icon: '🗃️' },
    { name: 'Settings', path: '/Settings', icon: '⚙️' }
  ];

  // ADMIN menüleri
  const adminMenuItems = [
    { name: 'Admin Dashboard', path: '/admin/dashboard', icon: '📈' },
    { name: 'Users', path: '/admin/users', icon: '👥' },
    { name: 'Tickets', path: '/admin/tickets', icon: '🗄️' },
    { name: 'Audit Logs', path: '/admin/logs', icon: '📄' },
    
  ];

  return (
    <aside className={`${isOpen ? 'w-64' : 'w-20'} bg-[#0747A6] text-white transition-all duration-300 flex flex-col relative shrink-0`}>
      <div className="p-4 flex items-center space-x-3">
        <div className="min-w-[40px] h-10 bg-white/20 rounded flex items-center justify-center font-bold text-xl">T</div>
        {isOpen && <span className="font-bold text-lg whitespace-nowrap">Ticket Master</span>}
      </div>

      <nav className="flex-1 mt-6 px-3 space-y-1">
        {/* Ortak  */}
        {menuItems.map((item) => (
          <MenuItem key={item.name} item={item} isOpen={isOpen} isActive={location.pathname === item.path} />
        ))}

        {/* ADMIN */}
        {currentUser?.role === 'ADMIN' && (
          <div className="mt-10 pt-4 border-t border-white/10">
            {isOpen && (
              <p className="px-3 mb-2 text-[10px] font-bold tracking-wider text-white/50 uppercase">
                Management
              </p>
            )}
            {adminMenuItems.map((item) => (
              <MenuItem key={item.name} item={item} isOpen={isOpen} isActive={location.pathname === item.path} />
            ))}
          </div>
        )}
      </nav>

      <button
        onClick={() => setIsOpen(!isOpen)}
        className="absolute -right-3 top-20 bg-white text-blue-800 rounded-full w-6 h-6 border shadow-md flex items-center justify-center text-[10px] z-20 hover:scale-110 transition-transform"
      >
        {isOpen ? '❮' : '❯'}
      </button>
    </aside>
  );
};


const MenuItem = ({ item, isOpen, isActive }) => (
  <Link
    to={item.path}
    className={`flex items-center p-3 rounded cursor-pointer transition-colors group ${isActive ? 'bg-white/20' : 'hover:bg-white/10'}`}
  >
    <span className="text-xl opacity-70 group-hover:opacity-100">{item.icon}</span>
    {isOpen && <span className="ml-4 text-sm font-medium">{item.name}</span>}
  </Link>
);

export default Sidebar;