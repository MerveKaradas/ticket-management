import Api from './Api';


export const getAuditLogs = (page = 0, size = 10, query = "") => {
    try {
        return Api.get(`admin/logs?page=${page}&size=${size}&query=${query}`);
    } catch (error) {
        console.error("Page hatası:", err);
    }
};

export const logoutAllUsers = () => {
    try {
        return Api.post("admin/logout-all");

    } catch (error) {
        console.error("Revoke hatası:", err);
    }
};


export const getAllUsers = (page = 0, size = 10,query = "") => {
    try {
        return Api.get(`users/getAllUsers?page=${page}&size=${size}&query=${query}`);

    } catch (error) {
        console.error("Hata :", err);
    }
    
};


export const getSummary = () => {
    try {
        return Api.get("dashboard/admin/summary");

    } catch (error) {
        console.error("Hata :", err);
    }
    
};