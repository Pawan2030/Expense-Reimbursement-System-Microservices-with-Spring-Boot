import { type FormEvent, useState } from 'react';
import { loginApi } from '../api/auth';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';

export default function Login() {
  const [username, setUsername] = useState(''); 
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const { login } = useAuth(); // removed claims here
  const navigate = useNavigate();

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      const token = await loginApi(username, password);
      login(token);

      // Decode JWT payload to get role
      const payload = JSON.parse(atob(token.split('.')[1]));
      const role = payload.role;

      if (role === 'MANAGER') navigate('/manager');
      else if (role === 'EMPLOYEE') navigate('/employee');
      else alert('Unknown role');
    } catch (err: any) {
      alert(err?.response?.data || 'Login failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-card auth-login-card">
      <h2>Login</h2>
      <form onSubmit={onSubmit}>
        <label>Username</label>
        <input 
          value={username} 
          onChange={e => setUsername(e.target.value)} 
          placeholder="Enter your username"
          autoFocus 
        />

        <label>Password</label>
        <div style={{ position: 'relative', width: '100%' }}>
          <input 
            type={showPassword ? 'text' : 'password'} 
            value={password} 
            onChange={e => setPassword(e.target.value)} 
            placeholder="Enter your password"
            style={{ width: '100%', paddingRight: '2.5rem', boxSizing: 'border-box' }}
          />
          <button 
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            style={{
              position: 'absolute',
              right: '0.5rem',
              top: '50%',
              transform: 'translateY(-50%)',
              border: 'none',
              background: '#60a5fa',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              padding: 0
            }}
          >
            {showPassword ? <AiOutlineEyeInvisible size={20} /> : <AiOutlineEye size={20} />}
          </button>
        </div>

        <button disabled={loading} type="submit" className="auth-button">
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>

      <div className="auth-footer">
        <span className="footer-text">No account?</span>
        <Link to="/register" className="footer-button">Register</Link>
      </div>
    </div>
  );
}
