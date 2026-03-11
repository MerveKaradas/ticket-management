import Api from './Api';

export const deleteUser = (id) => {
    try {
        return Api.delete(`/users/deleteUser/${id}`);
    } catch (error) {
        console.error("Hata :", err);
    }
  
};
