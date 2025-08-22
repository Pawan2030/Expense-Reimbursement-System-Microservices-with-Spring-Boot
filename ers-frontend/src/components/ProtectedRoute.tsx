import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { JSX } from 'react/jsx-runtime';

interface ProtectedRouteProps {
  children: JSX.Element;
  role: 'MANAGER' | 'EMPLOYEE';
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, role }) => {
  const { token, claims } = useAuth();

  // Redirect to login if no valid token or wrong role
  if (!token || !claims || claims.role !== role) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
