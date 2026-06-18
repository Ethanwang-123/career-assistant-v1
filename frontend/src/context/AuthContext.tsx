import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import { api } from '../api/api';

interface AuthContextValue {
  token: string | null;
  email: string | null;
  isAuthenticated: boolean;
  register: (email: string, password: string) => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [email, setEmail] = useState(() => localStorage.getItem('email'));

  async function register(emailValue: string, password: string) {
    const response = await api.register(emailValue, password);
    localStorage.setItem('token', response.token);
    localStorage.setItem('email', response.email);
    setToken(response.token);
    setEmail(response.email);
  }

  async function login(emailValue: string, password: string) {
    const response = await api.login(emailValue, password);
    localStorage.setItem('token', response.token);
    localStorage.setItem('email', response.email);
    setToken(response.token);
    setEmail(response.email);
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    setToken(null);
    setEmail(null);
  }

  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      email,
      isAuthenticated: Boolean(token),
      register,
      login,
      logout
    }),
    [token, email]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
