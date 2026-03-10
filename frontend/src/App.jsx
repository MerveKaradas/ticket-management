import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Home from "./pages/Home";
import Login from './pages/Login';
import Register from './pages/Register';
import Board from './pages/Board';
import Dashboard from './pages/Dashboard';

import Layout from './components/layout/Layout';
import TicketDetailModal from './components/modals/TicketDetailModal';
import PublicRoute from './components/PublicRoutes'; 
import AuditLogs from './pages/AuditLogs';
import UserManagement from './pages/UserManagement';
import AdminDashboard from './pages/AdminDashboard';

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('accessToken');
  return token ? children : <Navigate to="/home" />;
};

function App() {
  return (
    <>
      <ToastContainer />
      <BrowserRouter>
        <Routes>
          {/* Public */}
          <Route path="/" element={<PublicRoute><Home /></PublicRoute>} />
          <Route path="/home" element={<PublicRoute><Home /></PublicRoute>} />
          <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
          <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />

          {/* Private */}
          <Route path="/" element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }>
            <Route index element={<Dashboard />} />
            <Route path="dashboard" element={<Dashboard />} />
            
            <Route path="board" element={<Board />}>
              <Route path="ticket/:ticketId" element={<TicketDetailModal />} />
            </Route>
            <Route path="/admin/logs" element={<AuditLogs />} />
            <Route path="/admin/users" element={<UserManagement />} />
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
          </Route>

          {/* Tanımsız rotalar */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App;