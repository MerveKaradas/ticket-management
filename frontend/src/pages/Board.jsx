import FilterBar from '../components/dashboard/FilterBar';
import KanbanBoard from '../components/dashboard/KanbanBoard';
import CreateTicketModal from '../components/modals/CreateTicketModal';
import { useEffect, useState, useCallback } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import Api from '../services/Api';

const Board = () => {
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

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const navigate = useNavigate();

  const fetchTicketsData = useCallback(async () => {
    try {
      const params = { ...filters };

      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === null || params[key] === undefined) {
          delete params[key];
        }
      });

      const response = await Api.get('/tickets/filter', { params });
      setTickets(response.data.content || []);
    } catch (err) {
      console.error("Biletler getirilirken hata oluştu:", err.response?.data || err.message);
    }
  }, [filters]);

  // Filtreler değiştikçe tetiklenir
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchTicketsData();
    }, 400);
    return () => clearTimeout(timer);
  }, [fetchTicketsData]);

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


  const columns = [
    { title: 'REOPEN', statuses: ['REOPENED'], color: 'bg-orange-50/50' },
    { title: 'OPEN', statuses: ['OPEN'], color: 'bg-gray-100/50' },
    { title: 'IN PROGRESS', statuses: ['IN_PROGRESS'], color: 'bg-blue-50/50' },
    { title: 'DONE', statuses: ['DONE', 'COMPLETED', 'CLOSED'], color: 'bg-green-50/50' }
  ];

  const handleTicketClick = (ticket) => {
    navigate(`/board/ticket/${ticket.id}`);
  };

  return (

    <div className="relative flex-1 flex flex-col h-full min-w-0 overflow-hidden bg-white font-sans text-gray-900">
      <main className="relative flex-1 flex flex-col min-w-0 overflow-hidden bg-white">

        <div className="flex-1 p-6 overflow-hidden flex flex-col">
          <h2 className="text-2xl font-bold text-gray-800 mb-6 tracking-tight">Board</h2>

          <FilterBar
            filters={filters}
            setFilters={setFilters}
            users={users}
            currentUser={currentUser}
            onOpenCreateModal={() => setIsCreateModalOpen(true)}
          />

          <KanbanBoard
            tickets={tickets}
            columns={columns}
            onTicketClick={handleTicketClick}
          />
        </div>

        <div className="absolute inset-y-0 right-0 left-0 z-[100] bg-white empty:hidden">
          <Outlet context={{ refreshTickets: fetchTicketsData, currentUser }} />
        </div>
      </main>

      <CreateTicketModal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} refreshTickets={fetchTicketsData} />

    </div>
  );
};

export default Board;