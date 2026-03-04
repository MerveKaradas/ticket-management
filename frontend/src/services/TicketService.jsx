import api from './Api';

export const getTickets = async () => {
  try {
    const response = await api.get('/tickets/getAllTickets'); 
    return response.data;
  } catch (error) {
    console.error("Biletler çekilemedi:", error);
    throw error;
  }
};