import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  useRef,
  type ReactNode,
} from 'react';
import type { AuthTokens } from '@/api/auth';

export interface User {
  userId: string;
  username: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (tokens: AuthTokens) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function parseJwt(token: string): User | null {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64));
    // JWT structure: subject (sub) = userId, username claim = username
    return { userId: payload.sub, username: payload.username };
  } catch {
    return null;
  }
}

function getTokenExpiry(token: string): number | null {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64));
    return payload.exp ? payload.exp * 1000 : null;
  } catch {
    return null;
  }
}

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const refreshTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const clearRefreshTimeout = useCallback(() => {
    if (refreshTimeoutRef.current) {
      clearTimeout(refreshTimeoutRef.current);
      refreshTimeoutRef.current = null;
    }
  }, []);

  const logout = useCallback(() => {
    clearRefreshTimeout();
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    setToken(null);
    setUser(null);
  }, [clearRefreshTimeout]);

  const scheduleTokenRefresh = useCallback(
    (accessToken: string, refreshToken: string) => {
      clearRefreshTimeout();

      const expiry = getTokenExpiry(accessToken);
      if (!expiry) return;

      // Refresh 1 minute before expiry
      const refreshTime = expiry - Date.now() - 60000;
      if (refreshTime <= 0) {
        // Token is already expired or about to expire, refresh now
        performTokenRefresh(refreshToken);
        return;
      }

      refreshTimeoutRef.current = setTimeout(() => {
        performTokenRefresh(refreshToken);
      }, refreshTime);
    },
    [clearRefreshTimeout]
  );

  const performTokenRefresh = useCallback(
    async (refreshToken: string) => {
      try {
        // Import dynamically to avoid circular dependency
        const { authApi } = await import('@/api/auth');
        const tokens = await authApi.refresh(refreshToken);

        const parsedUser = parseJwt(tokens.accessToken);
        if (parsedUser) {
          localStorage.setItem('token', tokens.accessToken);
          localStorage.setItem('refreshToken', tokens.refreshToken);
          setToken(tokens.accessToken);
          setUser(parsedUser);
          scheduleTokenRefresh(tokens.accessToken, tokens.refreshToken);
        } else {
          logout();
        }
      } catch {
        // Refresh failed, log out
        logout();
      }
    },
    [logout, scheduleTokenRefresh]
  );

  // Initialize auth state from localStorage on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedRefreshToken = localStorage.getItem('refreshToken');

    if (storedToken && storedRefreshToken) {
      const parsedUser = parseJwt(storedToken);
      const expiry = getTokenExpiry(storedToken);

      if (parsedUser && expiry) {
        if (expiry > Date.now()) {
          // Token still valid
          setToken(storedToken);
          setUser(parsedUser);
          scheduleTokenRefresh(storedToken, storedRefreshToken);
        } else {
          // Token expired, try to refresh
          performTokenRefresh(storedRefreshToken);
        }
      } else {
        // Invalid token, remove it
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
      }
    }
    setIsLoading(false);
  }, [scheduleTokenRefresh, performTokenRefresh]);

  const login = useCallback(
    (tokens: AuthTokens) => {
      const parsedUser = parseJwt(tokens.accessToken);
      if (parsedUser) {
        localStorage.setItem('token', tokens.accessToken);
        localStorage.setItem('refreshToken', tokens.refreshToken);
        setToken(tokens.accessToken);
        setUser(parsedUser);
        scheduleTokenRefresh(tokens.accessToken, tokens.refreshToken);
      }
    },
    [scheduleTokenRefresh]
  );

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      clearRefreshTimeout();
    };
  }, [clearRefreshTimeout]);

  const value: AuthContextType = {
    user,
    token,
    isLoading,
    isAuthenticated: !!token && !!user,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
