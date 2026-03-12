import axios from 'axios';

const Api = axios.create({
  baseURL: 'http://localhost:8080/api', 
  withCredentials: true, 
});

Api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

Api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // KRİTİK NOKTA: Eğer hata alan istek login isteği ise REFRESH YAPMA!
    // Doğrudan hatayı Login.jsx içindeki catch bloğuna fırlatır.
    if (originalRequest.url.includes('/auth/login')) {
      return Promise.reject(error);
    }

    // 401 hatası geldiyse ve bu istek daha önce tekrar edilmediyse
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const { data } = await axios.post(
          'http://localhost:8080/api/auth/refresh',
          {},
          { withCredentials: true }
        );

        const newAccessToken = data.accessToken;
        localStorage.setItem('accessToken', newAccessToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return Api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default Api;