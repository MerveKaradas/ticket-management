import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from "./pages/Home"
import Login from './pages/Login';
import Register from './pages/Register';
import Board from './pages/Board';
import PublicRoute from './components/PublicRoutes';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Dashboard from './pages/Dashboard';


function App() {

  return (
    <>
    <ToastContainer />
    <Router>
      <Routes>
        <Route path="/" element={
          <PublicRoute>
            <Home />
          </PublicRoute>
        } />
        <Route path="/login" element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        } />
        
        <Route path="/register" element={
          <PublicRoute>
            <Register />
          </PublicRoute>
        } />
        <Route path="/board" element={<Board/>} />
        <Route path="/dashboard" element={<Dashboard/>} />
      </Routes>
    </Router>
      
    </>
  )
}

export default App
