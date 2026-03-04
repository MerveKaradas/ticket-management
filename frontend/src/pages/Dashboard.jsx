import FilterBar from '../components/dashboard/FilterBar';
import KanbanBoard from '../components/dashboard/KanbanBoard';
import StatsPanel from '../components/dashboard/StatsPanel';
import CreateTicketModal from '../components/modals/CreateTicketModal';
import LogoutConfirmModal from '../components/modals/LogoutConfirmModal';
import Sidebar from '../components/layout/Sidebar';
import { useEffect, useState } from 'react'; 
import { useDashboard } from '../hooks/useDashboard';
import { useNavigate } from 'react-router-dom';
import Header from '../components/layout/Header'; 
import TicketDetailModal from '../components/modals/TicketDetailModal';
import Api from '../services/Api';

const Dashboard = () => {
 const [currentUser, setCurrentUser] = useState(null);
  
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await Api.get('/users/getCurrentUser');
        setCurrentUser(response.data);
       
      } catch (err) {
        console.error("Kullanıcı bilgileri alınamadı:", err);
      }
    };
    fetchUser();
  }, []);

  const {
    filteredTickets, searchTerm, setSearchTerm,
    activeFilter, setActiveFilter, stats
  } = useDashboard(currentUser?.id);

  

  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const columns = [
    { title: 'REOPEN', statuses: ['REOPENED'], color: 'bg-orange-50/50' },
    { title: 'OPEN', statuses: ['OPEN'], color: 'bg-gray-100/50' },
    { title: 'IN PROGRESS', statuses: ['IN_PROGRESS'], color: 'bg-blue-50/50' },
    { title: 'DONE', statuses: ['DONE', 'COMPLETED', 'CLOSED'], color: 'bg-green-50/50' }
  ];

  const navigate = useNavigate();

  const confirmLogout = async () => {
    const currentRefreshToken = localStorage.getItem('refreshToken');
    try {
  
      await Api.post('/users/logout', { refreshToken: currentRefreshToken });
    } catch (err) {
      console.error("Logout hatası:", err);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      navigate('/');
    }
  };

  return (
    <div className="flex h-screen bg-white overflow-hidden font-sans text-gray-900">
      <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />

      <main className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header
          currentUser={currentUser}
          isProfileOpen={isProfileOpen}
          setIsProfileOpen={setIsProfileOpen}
          setIsLogoutModalOpen={setIsLogoutModalOpen}
        />

        <div className="flex-1 p-6 overflow-hidden flex flex-col">
          <h2 className="text-2xl font-bold text-gray-800 mb-6 tracking-tight">Board</h2>

          <StatsPanel stats={stats} />

          <FilterBar
            searchTerm={searchTerm}
            setSearchTerm={setSearchTerm}
            activeFilter={activeFilter}
            setActiveFilter={setActiveFilter}
            onOpenCreateModal={() => setIsCreateModalOpen(true)}
          />

          <KanbanBoard
            tickets={filteredTickets}
            columns={columns}
            onTicketClick={(ticket) => setSelectedTicket(ticket)} />
        </div>
      </main>

      <LogoutConfirmModal
        isOpen={isLogoutModalOpen}
        onClose={() => setIsLogoutModalOpen(false)}
        onConfirm={confirmLogout}
      />

      <CreateTicketModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      // refreshTickets={refreshTickets}
      />

      <TicketDetailModal
        currentUser={currentUser}
        ticket={selectedTicket}
        onClose={() => setSelectedTicket(null)}
      />
    </div>


  );
};

export default Dashboard; 