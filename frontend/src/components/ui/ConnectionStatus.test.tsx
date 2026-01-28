import { render, screen, act } from '@testing-library/react';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { ConnectionStatus } from './ConnectionStatus';

// Mock the WebSocketContext
const mockUseWebSocket = vi.fn();
vi.mock('@/context/WebSocketContext', () => ({
  useWebSocket: () => mockUseWebSocket(),
}));

describe('ConnectionStatus', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    mockUseWebSocket.mockReturnValue({ connectionState: 'connected' });
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.clearAllMocks();
  });

  describe('when connected', () => {
    it('renders nothing when connected', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connected' });

      const { container } = render(<ConnectionStatus />);

      expect(container.firstChild).toBeNull();
    });
  });

  describe('when connecting', () => {
    it('shows "Connecting..." indicator immediately', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connecting' });

      render(<ConnectionStatus />);

      expect(screen.getByText('Connecting...')).toBeInTheDocument();
    });

    it('displays pulsing warning indicator', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connecting' });

      render(<ConnectionStatus />);

      const indicator = document.querySelector('.animate-pulse.bg-warning');
      expect(indicator).toBeInTheDocument();
    });
  });

  describe('when disconnected', () => {
    it('does not show indicator immediately when disconnected', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });

      render(<ConnectionStatus />);

      expect(screen.queryByText('Disconnected')).not.toBeInTheDocument();
    });

    it('shows indicator after 5 seconds of being disconnected', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });

      render(<ConnectionStatus />);

      // Before 5 seconds - should not show
      act(() => {
        vi.advanceTimersByTime(4999);
      });
      expect(screen.queryByText('Disconnected')).not.toBeInTheDocument();

      // After 5 seconds - should show
      act(() => {
        vi.advanceTimersByTime(1);
      });
      expect(screen.getByText('Disconnected')).toBeInTheDocument();
    });

    it('displays error indicator when disconnected for 5+ seconds', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });

      render(<ConnectionStatus />);

      act(() => {
        vi.advanceTimersByTime(5000);
      });

      const indicator = document.querySelector('.bg-error');
      expect(indicator).toBeInTheDocument();
    });

    it('hides indicator when connection is restored', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });

      const { rerender } = render(<ConnectionStatus />);

      // Wait for indicator to appear
      act(() => {
        vi.advanceTimersByTime(5000);
      });
      expect(screen.getByText('Disconnected')).toBeInTheDocument();

      // Simulate reconnection
      mockUseWebSocket.mockReturnValue({ connectionState: 'connected' });
      rerender(<ConnectionStatus />);

      expect(screen.queryByText('Disconnected')).not.toBeInTheDocument();
    });

    it('resets timer when briefly disconnected then reconnected', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });

      const { rerender } = render(<ConnectionStatus />);

      // Disconnect for 3 seconds
      act(() => {
        vi.advanceTimersByTime(3000);
      });
      expect(screen.queryByText('Disconnected')).not.toBeInTheDocument();

      // Reconnect
      mockUseWebSocket.mockReturnValue({ connectionState: 'connected' });
      rerender(<ConnectionStatus />);

      // Disconnect again
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });
      rerender(<ConnectionStatus />);

      // Wait 3 more seconds (less than 5 from new disconnect)
      act(() => {
        vi.advanceTimersByTime(3000);
      });
      expect(screen.queryByText('Disconnected')).not.toBeInTheDocument();

      // Wait 2 more seconds (5 total from new disconnect)
      act(() => {
        vi.advanceTimersByTime(2000);
      });
      expect(screen.getByText('Disconnected')).toBeInTheDocument();
    });
  });

  describe('transition from connecting to connected', () => {
    it('hides connecting indicator when connected', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connecting' });

      const { rerender } = render(<ConnectionStatus />);
      expect(screen.getByText('Connecting...')).toBeInTheDocument();

      mockUseWebSocket.mockReturnValue({ connectionState: 'connected' });
      rerender(<ConnectionStatus />);

      expect(screen.queryByText('Connecting...')).not.toBeInTheDocument();
    });
  });

  describe('transition from connecting to disconnected', () => {
    it('waits 5 seconds before showing disconnected after failing to connect', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connecting' });

      const { rerender } = render(<ConnectionStatus />);
      expect(screen.getByText('Connecting...')).toBeInTheDocument();

      // Connection fails
      mockUseWebSocket.mockReturnValue({ connectionState: 'disconnected' });
      rerender(<ConnectionStatus />);

      // Connecting indicator gone, but disconnected not showing yet
      expect(screen.queryByText('Connecting...')).not.toBeInTheDocument();
      expect(screen.queryByText('Disconnected')).not.toBeInTheDocument();

      // After 5 seconds
      act(() => {
        vi.advanceTimersByTime(5000);
      });
      expect(screen.getByText('Disconnected')).toBeInTheDocument();
    });
  });

  describe('UI positioning', () => {
    it('renders in fixed position at bottom-left', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connecting' });

      render(<ConnectionStatus />);

      const container = screen.getByText('Connecting...').closest('.fixed');
      expect(container).toHaveClass('bottom-4', 'left-4');
    });

    it('has z-index for overlay behavior', () => {
      mockUseWebSocket.mockReturnValue({ connectionState: 'connecting' });

      render(<ConnectionStatus />);

      const container = screen.getByText('Connecting...').closest('.fixed');
      expect(container).toHaveClass('z-50');
    });
  });
});
