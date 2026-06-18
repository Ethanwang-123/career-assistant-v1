import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function AppLayout() {
  const { email, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <h1>Career Assistant</h1>
          <p>{email}</p>
        </div>
        <nav>
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/applications">Applications</NavLink>
          <NavLink to="/ai-analysis">JD Analysis</NavLink>
        </nav>
        <button className="secondary-button" onClick={handleLogout}>
          Log out
        </button>
      </aside>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
