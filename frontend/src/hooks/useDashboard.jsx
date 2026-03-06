import { useState, useEffect, useMemo } from 'react';
import { getTickets } from '../services/TicketService';

export const useDashboard = (currentUserId) => {
  const [tickets, setTickets] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [isLoading, setIsLoading] = useState(true);


  const fetchTickets = async () => {
    setIsLoading(true);
    try {
      const data = await getTickets();
      setTickets(data || []);
    } catch (err) {
      console.error("Bilet çekme hatası:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchTickets();
  }, []);

  const filteredTickets = useMemo(() => {
    let result = [...tickets];

    if (searchTerm) {
      result = result.filter(t =>
        t.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        t.id.toString().toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (activeFilter === 'MY_TICKETS') {
      result = result.filter(t => t.assignedTo?.id === currentUserId);
    } else if (activeFilter === 'HIGH') {
      result = result.filter(t => t.priority === 'HIGH');
    }

    return result;
  }, [tickets, searchTerm, activeFilter, currentUserId]);

  // İstatistik Hesaplamaları
  const stats = useMemo(() => {
    const total = tickets.length;
    const active = tickets.filter(t => ['OPEN', 'REOPENED', 'IN_PROGRESS'].includes(t.status)).length;
    const urgent = tickets.filter(t => t.priority === 'HIGH').length;
    const completed = tickets.filter(t => t.status === 'DONE').length;
    
    return {
      total,
      active,
      urgent,
      completedPercent: total > 0 ? Math.round((completed / total) * 100) : 0
    };
  }, [tickets]);

  return {
    tickets,
    filteredTickets,
    searchTerm,
    setSearchTerm,
    activeFilter,
    setActiveFilter,
    stats,
    isLoading,
    refreshTickets: fetchTickets 
  };
};