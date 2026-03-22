import Api from './Api';

export const getAllTickets = () => {
    try {
        return Api.get(`tickets/getAllTickets`);

    } catch (error) {
        console.error("Hata :", err);
    }

};


export const deleteAllTickets = () => {
    try {
        return Api.delete('/tickets/deleteAllTickets');
    } catch (error) {
        console.error("Hata :", err);
    }

};


export const deleteTicket = (id) => {
    try {
        return Api.delete(`/tickets/deleteTicket/${id}`);
    } catch (error) {
        console.error("Hata :", err);
    }

};


export const filterTickets = (title, status, priority, page, size) => {
    const params = new URLSearchParams({
        page,
        size,
        ...(title && { title }),
        ...(status && { status }),
        ...(priority && { priority })
    });
    return Api.get(`/tickets/filter?${params.toString()}`);
};

export const claimTicket = async (ticketId, claimTitle) => {
    try {
        return await Api.post(`/tickets/${ticketId}/claim`, {
            newTitle: claimTitle
        });
    } catch (error) {
        console.error("Bilet üstlenilemedi:", error);
        throw error;
    }
};