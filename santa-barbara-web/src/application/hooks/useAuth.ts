import { useState, useCallback, useEffect } from 'react';

export interface User {
  id: string;
  username: string;
  email?: string;
}

export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  acceptInvite: (username: string, password: string) => Promise<void>;
}

export function useAuth(): AuthContextType {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Verificar se há usuário salvo no localStorage ao carregar
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
        setIsAuthenticated(true);
      } catch {
        localStorage.removeItem('user');
      }
    }
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    setIsLoading(true);
    try {
      // TODO: Integrar com API real
      // const response = await api.post('/auth/login', { username, password });
      // const userData = response.data.user;

      // Simulação
      const userData: User = {
        id: '1',
        username,
        email: `${username}@example.com`,
      };

      setUser(userData);
      setIsAuthenticated(true);
      localStorage.setItem('user', JSON.stringify(userData));
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem('user');
    // TODO: Integrar com API real para logout
    // await api.post('/auth/logout');
  }, []);

  const acceptInvite = useCallback(
    async (username: string, password: string) => {
      setIsLoading(true);
      try {
        // TODO: Integrar com API real
        // const response = await api.post('/auth/invite/accept', { username, password });
        // const userData = response.data.user;

        // Simulação
        const userData: User = {
          id: '1',
          username,
          email: `${username}@example.com`,
        };

        setUser(userData);
        setIsAuthenticated(true);
        localStorage.setItem('user', JSON.stringify(userData));
      } catch (error) {
        console.error('Accept invite failed:', error);
        throw error;
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  return {
    user,
    isAuthenticated,
    isLoading,
    login,
    logout,
    acceptInvite,
  };
}
