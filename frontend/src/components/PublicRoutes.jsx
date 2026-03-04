import { Navigate } from 'react-router-dom';


const PublicRoute = ({ children }) => {
  const token = localStorage.getItem('token');

  if (token) {
    // Eğer token varsa login/register sayfasına gitmeye çalışanı dashboard'a yönlendirir
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default PublicRoute;