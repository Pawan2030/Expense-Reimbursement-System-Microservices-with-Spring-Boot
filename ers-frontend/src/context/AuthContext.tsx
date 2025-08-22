import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { decodeJwt, isExpired, type JwtClaims } from '../utils/jwt';
import { useNavigate } from 'react-router-dom';

type AuthContextType = {
  token: string | null;
  claims: JwtClaims | null;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
  token: null,
  claims: null,
  login: () => {},
  logout: () => {},
});

const TOKEN_KEY = 'authToken';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const navigate = useNavigate();
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(TOKEN_KEY));

  const claims = useMemo(() => (token ? decodeJwt(token) : null), [token]);

  // Auto logout on token expiry
  useEffect(() => {
    if (!token) return;
    if (isExpired(claims)) {
      localStorage.removeItem(TOKEN_KEY);
      setToken(null);
      navigate('/login', { replace: true });
    }
  }, [token, claims, navigate]);

  const login = (t: string) => {
    // Store only token, never store password
    localStorage.setItem(TOKEN_KEY, t);
    setToken(t);
    navigate('/dashboard', { replace: true }); // navigate after login
  };

const logout = () => {
  localStorage.removeItem(TOKEN_KEY);
  setToken(null);

  // Reset history stack to prevent back navigation
  window.history.pushState(null, '', '/login');
  window.onpopstate = () => window.history.go(1);

  // Navigate using React Router
  navigate('/login', { replace: true });
};


  return (
    <AuthContext.Provider value={{ token, claims, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook for components
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);

// Helper for APIs
// eslint-disable-next-line react-refresh/only-export-components
export const getAuthToken = () => localStorage.getItem(TOKEN_KEY);
