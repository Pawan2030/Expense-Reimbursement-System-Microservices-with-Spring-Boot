import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { JSX } from 'react';

export default function ProtectedRoute({ role, children }: { role?: 'EMPLOYEE'|'MANAGER', children: JSX.Element }) {
  const { token, claims } = useAuth();

  if (!token || !claims) return <Navigate to="/login" replace />;

  if (role && claims.role !== role) {
    return <Navigate to={claims.role === 'MANAGER' ? '/manager' : '/employee'} replace />;
  }

  return children;
}
