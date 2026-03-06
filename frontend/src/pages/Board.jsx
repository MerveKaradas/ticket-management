import FilterBar from '../components/dashboard/FilterBar';
import KanbanBoard from '../components/dashboard/KanbanBoard';
import StatsPanel from '../components/dashboard/StatsPanel';
import CreateTicketModal from '../components/modals/CreateTicketModal';
import LogoutConfirmModal from '../components/modals/LogoutConfirmModal';
import Sidebar from '../components/layout/Sidebar';
import { useEffect, useState, useCallback } from 'react';
import { useDashboard } from '../hooks/useDashboard';
import { useNavigate } from 'react-router-dom';
import Header from '../components/layout/Header';
import TicketDetailModal from '../components/modals/TicketDetailModal';
import Api from '../services/Api';

const Board = () => {
  // 1. Önce State tanımları
  const [currentUser, setCurrentUser] = useState(null);
  const [users, setUsers] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [filters, setFilters] = useState({
    title: '',
    priority: '',
    assignedToId: '',
    status: '',
    page: 0,
    size: 50
  });

  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const navigate = useNavigate();

  // 2. Merkezi Fetch Fonksiyonu
const fetchTicketsData = useCallback(async () => {
  try {
    const params = { ...filters };

    // Backend'e boş string veya geçersiz değer gitmesini önle
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });

    // Artık sadece tek bir endpoint var!
    const response = await Api.get('/tickets/filter', { params });
    setTickets(response.data.content || []);
  } catch (err) {
    console.error("Biletler getirilirken hata oluştu:", err.response?.data || err.message);
  }
}, [filters]);

  // 3. Debounce Effect (Filtreler değiştikçe tetiklenir)
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchTicketsData();
    }, 400);
    return () => clearTimeout(timer);
  }, [fetchTicketsData]);

  // 4. İlk Verileri Yükleme
  useEffect(() => {
    const init = async () => {
      try {
        const [userRes, usersRes] = await Promise.all([
          Api.get('/users/getCurrentUser'),
          Api.get('/users/listForAssignment')
        ]);
        setCurrentUser(userRes.data);
        setUsers(usersRes.data);
      } catch (err) {
        console.error("Veri çekme hatası:", err);
      }
    };
    init();
  }, []);

  const { stats } = useDashboard(currentUser?.id);

  const columns = [
    { title: 'REOPEN', statuses: ['REOPENED'], color: 'bg-orange-50/50' },
    { title: 'OPEN', statuses: ['OPEN'], color: 'bg-gray-100/50' },
    { title: 'IN PROGRESS', statuses: ['IN_PROGRESS'], color: 'bg-blue-50/50' },
    { title: 'DONE', statuses: ['DONE', 'COMPLETED', 'CLOSED'], color: 'bg-green-50/50' }
  ];

  const confirmLogout = async () => {
    try {
      await Api.post('/users/logout', { refreshToken: localStorage.getItem('refreshToken') });
      localStorage.clear();
      navigate('/');
    } catch (err) {
      navigate('/');
    }
  };

  return (
    <div className="flex h-screen bg-white overflow-hidden font-sans text-gray-900">
      <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
      <main className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header currentUser={currentUser} isProfileOpen={isProfileOpen} setIsProfileOpen={setIsProfileOpen} setIsLogoutModalOpen={setIsLogoutModalOpen} />
        <div className="flex-1 p-6 overflow-hidden flex flex-col">
          <h2 className="text-2xl font-bold text-gray-800 mb-6 tracking-tight">Board</h2>
          <StatsPanel stats={stats} />
          
          <FilterBar 
            filters={filters} 
            setFilters={setFilters} 
            users={users} 
            onOpenCreateModal={() => setIsCreateModalOpen(true)} 
          />

          <KanbanBoard 
            tickets={tickets} 
            columns={columns} 
            onTicketClick={(ticket) => setSelectedTicket(ticket)} 
          />
        </div>
      </main>

      <LogoutConfirmModal isOpen={isLogoutModalOpen} onClose={() => setIsLogoutModalOpen(false)} onConfirm={confirmLogout} />
      <CreateTicketModal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} refreshTickets={fetchTicketsData} />
      {/* Sadece selectedTicket varsa modalı göster */}
{selectedTicket && (
  <TicketDetailModal 
    currentUser={currentUser} 
    ticket={selectedTicket} 
    onClose={() => setSelectedTicket(null)} 
    refreshTickets={fetchTicketsData} 
  />
)}
    </div>
  );
};

export default Board;