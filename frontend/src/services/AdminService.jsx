import Api from './Api';


export const getAuditLogs = (page = 0, size = 10) => {
    try {
        return Api.get(`admin/logs?page=${page}&size=${size}`);
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
