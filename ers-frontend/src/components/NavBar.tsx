import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './NavBar.css'; // Optional: separate CSS for navbar

export default function NavBar() {
  const { claims, logout } = useAuth();
  const navigate = useNavigate();

  const onLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <nav className="nav">
      <div className="brand">Employee Reimbursement System</div>
      <div className="links">
        {!claims && (
          <>
            <Link className="nav-link" to="/login">Login</Link>
            <Link className="nav-link" to="/register">Register</Link>
          </>
        )}
        {claims?.role === 'MANAGER' && <Link className="nav-link" to="/manager">Manager</Link>}
        {claims?.role === 'EMPLOYEE' && <Link className="nav-link" to="/employee">Employee</Link>}
        {claims && <button className="nav-button" onClick={onLogout}>Logout</button>}
      </div>
    </nav>
  );
}
