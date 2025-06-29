// src/App.jsx
import React, { useState, useEffect } from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import ExamsList from './components/ExamsList';
import ExamForm from './components/ExamForm'; // <--- Import ExamForm
import AuthService from './services/auth.service';
import './App.css';

// --- PLACEHOLDER COMPONENTS (DEFINED HERE - ensure these are in your file) ---
const Home = () => <h2>Welcome to the Exam Registration Platform!</h2>;
const Dashboard = () => {
  const user = AuthService.getCurrentUser();
  return (
    <div>
      <h2>Dashboard</h2>
      {user && user.roles && user.roles.includes('ROLE_ADMIN') && <p>Welcome, Admin!</p>}
      {user && user.roles && user.roles.includes('ROLE_STUDENT') && <p>Welcome, Student!</p>}
      <p>You are logged in as: {user ? user.username : 'Guest'}</p>
    </div>
  );
};
// const Exams = () => <h2>Exams List (Coming Soon!)</h2>; // This is now ExamsList
const MyRegistrations = () => {
  const user = AuthService.getCurrentUser();
  return (
    <div>
      <h2>My Registrations</h2>
      <p>This is where {user ? user.username : 'your'} registrations will appear.</p>
      <p> (Implementation coming soon!)</p>
    </div>
  );
};
const AdminPanel = () => {
  return (
    <div>
      <h2>Admin Panel</h2>
      <p>This is the administration area.</p>
      <p> (Implementation coming soon!)</p>
    </div>
  );
};

// --- PrivateRoute (Keep this as is) ---
const PrivateRoute = ({ children, allowedRoles }) => {
  const currentUser = AuthService.getCurrentUser();
  const navigate = useNavigate();

  useEffect(() => {
    if (!currentUser || !currentUser.token) {
      navigate('/login');
    } else if (allowedRoles && currentUser.roles) {
      const hasPermission = allowedRoles.some(role => currentUser.roles.includes(role));
      if (!hasPermission) {
        navigate('/');
      }
    }
  }, [currentUser, navigate, allowedRoles]);

  if (!currentUser || !currentUser.token) {
    return null;
  }
  if (allowedRoles && currentUser.roles) {
    const hasPermission = allowedRoles.some(role => currentUser.roles.includes(role));
    if (!hasPermission) {
      return null;
    }
  }

  return children;
};


function App() {
  const [currentUser, setCurrentUser] = useState(undefined);
  const navigate = useNavigate();

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
  }, []);

  const logOut = () => {
    AuthService.logout();
    setCurrentUser(undefined);
    navigate('/login');
  };

  const showAdminBoard = currentUser && currentUser.roles && currentUser.roles.includes('ROLE_ADMIN');
  const showStudentBoard = currentUser && currentUser.roles && currentUser.roles.includes('ROLE_STUDENT');

  return (
    <div className="App">
      <nav className="navbar" style={{ backgroundColor: '#333', padding: '10px 20px', color: 'white', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Link to="/" className="navbar-brand" style={{ color: 'white', textDecoration: 'none', fontSize: '1.5em' }}>
          ExamReg
        </Link>
        <div className="navbar-nav">
          <Link to="/exams" className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
            Exams
          </Link>
          {showStudentBoard && (
            <Link to="/my-registrations" className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
              My Registrations
            </Link>
          )}
          {showAdminBoard && (
            <Link to="/admin" className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
              Admin Panel
            </Link>
          )}
        </div>

        <div className="navbar-nav ml-auto">
          {currentUser ? (
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Link to="/dashboard" className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
                {currentUser.username} (Dashboard)
              </Link>
              <a href="/login" onClick={logOut} className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
                Logout
              </a>
            </div>
          ) : (
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Link to="/login" className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
                Login
              </Link>
              <Link to="/register" className="nav-link" style={{ color: 'white', textDecoration: 'none', margin: '0 10px' }}>
                Register
              </Link>
            </div>
          )}
        </div>
      </nav>

      <div className="container" style={{ padding: '20px' }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected Routes */}
          <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/exams" element={<PrivateRoute><ExamsList /></PrivateRoute>} />

          {/* NEW ROUTES FOR EXAM FORM (ADMIN ONLY) */}
          <Route path="/exams/create" element={<PrivateRoute allowedRoles={['ROLE_ADMIN']}><ExamForm /></PrivateRoute>} />
          <Route path="/exams/edit/:id" element={<PrivateRoute allowedRoles={['ROLE_ADMIN']}><ExamForm /></PrivateRoute>} />

          <Route path="/my-registrations" element={<PrivateRoute allowedRoles={['ROLE_STUDENT']}><MyRegistrations /></PrivateRoute>} />
          <Route path="/admin" element={<PrivateRoute allowedRoles={['ROLE_ADMIN']}><AdminPanel /></PrivateRoute>} />

          {/* Fallback for unknown paths */}
          <Route path="*" element={<div>404 - Page Not Found</div>} />
        </Routes>
      </div>
    </div>
  );
}

export default App;