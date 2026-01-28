import { render, screen, act, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { AuthProvider, useAuth } from './AuthContext';

// Helper component to test the hook
function TestComponent() {
  const { user, token, isLoading, isAuthenticated, login, logout } = useAuth();
  return (
    <div>
      <span data-testid="isLoading">{String(isLoading)}</span>
      <span data-testid="isAuthenticated">{String(isAuthenticated)}</span>
      <span data-testid="userId">{user?.userId || 'null'}</span>
      <span data-testid="username">{user?.username || 'null'}</span>
      <span data-testid="token">{token || 'null'}</span>
      <button onClick={() => login(validToken)} data-testid="loginBtn">
        Login
      </button>
      <button onClick={logout} data-testid="logoutBtn">
        Logout
      </button>
    </div>
  );
}

// Valid JWT token with payload: { sub: 'user-123', username: 'testuser', iat: 1234567890, exp: 9999999999 }
// Base64 encoded payload: eyJzdWIiOiJ1c2VyLTEyMyIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJpYXQiOjEyMzQ1Njc4OTAsImV4cCI6OTk5OTk5OTk5OX0
const validToken =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJpYXQiOjEyMzQ1Njc4OTAsImV4cCI6OTk5OTk5OTk5OX0.signature';

// Invalid token (malformed)
const invalidToken = 'not-a-valid-jwt';

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Reset localStorage mock
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);
  });

  describe('Initial state', () => {
    it('starts with loading state true, then becomes false', async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      // After initial effect runs, loading should be false
      await waitFor(() => {
        expect(screen.getByTestId('isLoading').textContent).toBe('false');
      });
    });

    it('starts unauthenticated when no token in localStorage', async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      await waitFor(() => {
        expect(screen.getByTestId('isAuthenticated').textContent).toBe('false');
        expect(screen.getByTestId('userId').textContent).toBe('null');
        expect(screen.getByTestId('username').textContent).toBe('null');
        expect(screen.getByTestId('token').textContent).toBe('null');
      });
    });

    it('restores auth state from valid localStorage token', async () => {
      (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(validToken);

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      await waitFor(() => {
        expect(screen.getByTestId('isAuthenticated').textContent).toBe('true');
        expect(screen.getByTestId('userId').textContent).toBe('user-123');
        expect(screen.getByTestId('username').textContent).toBe('testuser');
        expect(screen.getByTestId('token').textContent).toBe(validToken);
      });
    });

    it('removes invalid token from localStorage on mount', async () => {
      (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(invalidToken);

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      await waitFor(() => {
        expect(screen.getByTestId('isAuthenticated').textContent).toBe('false');
        expect(localStorage.removeItem).toHaveBeenCalledWith('token');
      });
    });
  });

  describe('login', () => {
    it('logs in user with valid token', async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      await waitFor(() => {
        expect(screen.getByTestId('isLoading').textContent).toBe('false');
      });

      act(() => {
        screen.getByTestId('loginBtn').click();
      });

      expect(screen.getByTestId('isAuthenticated').textContent).toBe('true');
      expect(screen.getByTestId('userId').textContent).toBe('user-123');
      expect(screen.getByTestId('username').textContent).toBe('testuser');
      expect(screen.getByTestId('token').textContent).toBe(validToken);
      expect(localStorage.setItem).toHaveBeenCalledWith('token', validToken);
    });

    it('does not log in with invalid token', async () => {
      // Create a component that tries to login with invalid token
      function InvalidLoginTest() {
        const { isAuthenticated, login } = useAuth();
        return (
          <div>
            <span data-testid="isAuthenticated">{String(isAuthenticated)}</span>
            <button onClick={() => login(invalidToken)} data-testid="loginBtn">
              Login
            </button>
          </div>
        );
      }

      render(
        <AuthProvider>
          <InvalidLoginTest />
        </AuthProvider>
      );

      await waitFor(() => {
        expect(screen.getByTestId('isAuthenticated').textContent).toBe('false');
      });

      act(() => {
        screen.getByTestId('loginBtn').click();
      });

      // Should remain unauthenticated
      expect(screen.getByTestId('isAuthenticated').textContent).toBe('false');
      expect(localStorage.setItem).not.toHaveBeenCalled();
    });
  });

  describe('logout', () => {
    it('clears auth state on logout', async () => {
      (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(validToken);

      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      // Wait for initial auth state to load
      await waitFor(() => {
        expect(screen.getByTestId('isAuthenticated').textContent).toBe('true');
      });

      act(() => {
        screen.getByTestId('logoutBtn').click();
      });

      expect(screen.getByTestId('isAuthenticated').textContent).toBe('false');
      expect(screen.getByTestId('userId').textContent).toBe('null');
      expect(screen.getByTestId('username').textContent).toBe('null');
      expect(screen.getByTestId('token').textContent).toBe('null');
      expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    });
  });

  describe('useAuth hook', () => {
    it('throws error when used outside AuthProvider', () => {
      // Suppress console.error for this test
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

      expect(() => {
        render(<TestComponent />);
      }).toThrow('useAuth must be used within an AuthProvider');

      consoleSpy.mockRestore();
    });
  });
});
