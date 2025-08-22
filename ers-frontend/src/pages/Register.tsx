import { type FormEvent, useState } from 'react';
import { registerApi } from '../api/auth';
import { Link, useNavigate } from 'react-router-dom';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai'; // <- import icons

export default function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false); // <- toggle state
  const [role, setRole] = useState<'EMPLOYEE'|'MANAGER'>('EMPLOYEE');
  const [employeeId, setEmployeeId] = useState<number | ''>('');
  const [managerId, setManagerId] = useState<number | ''>('');
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    if (!username || !password) return alert('Enter username & password');
    if (role === 'EMPLOYEE' && (managerId === '' || employeeId === '')) {
      return alert('Employee requires managerId and employeeId');
    }
    setLoading(true);
    try {
      await registerApi({
        username,
        password,
        role,
        employeeId: employeeId === '' ? undefined : Number(employeeId),
        managerId: managerId === '' ? undefined : Number(managerId),
      });
      alert('Registered! Now login.');
      nav('/login');
    } catch (err: any) {
      alert(err?.response?.data || 'Register failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-card">
      <h2>Register</h2>
      <form onSubmit={onSubmit}>
        <label>Username</label>
        <input value={username} onChange={e => setUsername(e.target.value)} />

        <label>Password</label>
        <div style={{ position: 'relative', width: '100%' }}>
          <input 
            type={showPassword ? 'text' : 'password'} 
            value={password} 
            onChange={e => setPassword(e.target.value)} 
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

        <label>Role</label>
        <select value={role} onChange={e => setRole(e.target.value as any)}>
          <option value="EMPLOYEE">EMPLOYEE</option>
          <option value="MANAGER">MANAGER</option>
        </select>

        <label>Employee ID (for EMPLOYEE)</label>
        <input type="number" value={employeeId} onChange={e => setEmployeeId(e.target.value === '' ? '' : Number(e.target.value))} />

        <label>Manager ID (required if EMPLOYEE)</label>
        <input type="number" value={managerId} onChange={e => setManagerId(e.target.value === '' ? '' : Number(e.target.value))} />

        <button disabled={loading} type="submit">{loading ? '...' : 'Register'}</button>
      </form>
      <div className="auth-footer">
        <span className="footer-text">Have an account?</span>
        <Link to="/login" className="footer-button">Login</Link>
     </div>
    </div>
  );
}
