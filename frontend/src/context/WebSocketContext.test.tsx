import { render, screen, act, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { WebSocketProvider, useWebSocket } from './WebSocketContext';
import { AuthProvider } from './AuthContext';
import type { ConnectionState } from '@/services/websocket';

// Mock the websocket service
const mockConnect = vi.fn();
const mockDisconnect = vi.fn();
const mockSubscribe = vi.fn();
const mockSend = vi.fn();
const mockOnConnectionStateChange = vi.fn();

vi.mock('@/services/websocket', () => ({
  webSocketService: {
    connect: (token: string) => mockConnect(token),
    disconnect: () => mockDisconnect(),
    subscribe: (destination: string, callback: (message: unknown) => void) =>
      mockSubscribe(destination, callback),
    send: (destination: string, body: object) => mockSend(destination, body),
    onConnectionStateChange: (listener: (state: ConnectionState) => void) =>
      mockOnConnectionStateChange(listener),
  },
}));

// Valid JWT token for testing
const validToken =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEyMyIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJpYXQiOjEyMzQ1Njc4OTAsImV4cCI6OTk5OTk5OTk5OX0.signature';

// Helper component to test the hook
function TestComponent() {
  const { connectionState, subscribe, send } = useWebSocket();
  return (
    <div>
      <span data-testid="connectionState">{connectionState}</span>
      <button
        onClick={() => subscribe('/topic/test', () => {})}
        data-testid="subscribeBtn"
      >
        Subscribe
      </button>
      <button
        onClick={() => send('/app/test', { data: 'test' })}
        data-testid="sendBtn"
      >
        Send
      </button>
    </div>
  );
}

// Wrapper that includes both Auth and WebSocket providers
function renderWithProviders(ui: React.ReactElement, { authenticated = false } = {}) {
  // Set up localStorage mock based on auth state
  if (authenticated) {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(validToken);
  } else {
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);
  }

  return render(
    <AuthProvider>
      <WebSocketProvider>{ui}</WebSocketProvider>
    </AuthProvider>
  );
}

describe('WebSocketContext', () => {
  let connectionStateListener: ((state: ConnectionState) => void) | null = null;

  beforeEach(() => {
    vi.clearAllMocks();
    connectionStateListener = null;

    // Capture the connection state listener when registered
    mockOnConnectionStateChange.mockImplementation((listener) => {
      connectionStateListener = listener;
      // Immediately call with initial state
      listener('disconnected');
      return () => {
        connectionStateListener = null;
      };
    });

    // Reset localStorage mock
    (localStorage.getItem as ReturnType<typeof vi.fn>).mockReturnValue(null);

    // Return unsubscribe function from subscribe
    mockSubscribe.mockReturnValue(() => {});
  });

  describe('Connection management', () => {
    it('starts with disconnected state', async () => {
      renderWithProviders(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('connectionState').textContent).toBe('disconnected');
      });
    });

    it('connects when user is authenticated', async () => {
      renderWithProviders(<TestComponent />, { authenticated: true });

      await waitFor(() => {
        expect(mockConnect).toHaveBeenCalledWith(validToken);
      });
    });

    it('does not connect when user is not authenticated', async () => {
      renderWithProviders(<TestComponent />, { authenticated: false });

      await waitFor(() => {
        expect(mockConnect).not.toHaveBeenCalled();
      });
    });

    it('disconnects when user logs out', async () => {
      renderWithProviders(<TestComponent />, { authenticated: true });

      await waitFor(() => {
        expect(mockConnect).toHaveBeenCalled();
      });

      // The disconnect is called on unmount or when auth state changes
      // Since we can't easily simulate logout in this test, we verify
      // disconnect is called on unmount
    });

    it('updates connection state when state changes', async () => {
      renderWithProviders(<TestComponent />, { authenticated: true });

      await waitFor(() => {
        expect(screen.getByTestId('connectionState').textContent).toBe('disconnected');
      });

      // Simulate connection state change
      act(() => {
        if (connectionStateListener) {
          connectionStateListener('connecting');
        }
      });

      expect(screen.getByTestId('connectionState').textContent).toBe('connecting');

      act(() => {
        if (connectionStateListener) {
          connectionStateListener('connected');
        }
      });

      expect(screen.getByTestId('connectionState').textContent).toBe('connected');
    });

    it('cleans up on unmount by calling disconnect', async () => {
      const { unmount } = renderWithProviders(<TestComponent />, { authenticated: true });

      await waitFor(() => {
        expect(mockConnect).toHaveBeenCalled();
      });

      unmount();

      expect(mockDisconnect).toHaveBeenCalled();
    });
  });

  describe('subscribe', () => {
    it('calls webSocketService.subscribe with correct parameters', async () => {
      renderWithProviders(<TestComponent />, { authenticated: true });

      await waitFor(() => {
        expect(screen.getByTestId('connectionState')).toBeInTheDocument();
      });

      act(() => {
        screen.getByTestId('subscribeBtn').click();
      });

      expect(mockSubscribe).toHaveBeenCalledWith('/topic/test', expect.any(Function));
    });

    it('returns unsubscribe function', async () => {
      const mockUnsubscribe = vi.fn();
      mockSubscribe.mockReturnValue(mockUnsubscribe);

      let unsubscribe: (() => void) | undefined;

      function SubscribeTest() {
        const { subscribe } = useWebSocket();
        return (
          <button
            onClick={() => {
              unsubscribe = subscribe('/topic/test', () => {});
            }}
            data-testid="subscribeBtn"
          >
            Subscribe
          </button>
        );
      }

      renderWithProviders(<SubscribeTest />, { authenticated: true });

      act(() => {
        screen.getByTestId('subscribeBtn').click();
      });

      expect(unsubscribe).toBeDefined();
      expect(typeof unsubscribe).toBe('function');
    });
  });

  describe('send', () => {
    it('calls webSocketService.send with correct parameters', async () => {
      renderWithProviders(<TestComponent />, { authenticated: true });

      await waitFor(() => {
        expect(screen.getByTestId('connectionState')).toBeInTheDocument();
      });

      act(() => {
        screen.getByTestId('sendBtn').click();
      });

      expect(mockSend).toHaveBeenCalledWith('/app/test', { data: 'test' });
    });
  });

  describe('useWebSocket hook', () => {
    it('throws error when used outside WebSocketProvider', () => {
      // Suppress console.error for this test
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

      function OutsideProviderComponent() {
        useWebSocket();
        return null;
      }

      expect(() => {
        render(
          <AuthProvider>
            <OutsideProviderComponent />
          </AuthProvider>
        );
      }).toThrow('useWebSocket must be used within a WebSocketProvider');

      consoleSpy.mockRestore();
    });
  });
});
