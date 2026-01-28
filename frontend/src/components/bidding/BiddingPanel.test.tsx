import { render, screen, fireEvent, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { BiddingPanel } from './BiddingPanel';

// Mock the contexts
const mockIsAuthenticated = vi.fn();
const mockUser = vi.fn();
const mockSend = vi.fn();
const mockConnectionState = vi.fn();

vi.mock('@/context/AuthContext', () => ({
  useAuth: () => ({
    isAuthenticated: mockIsAuthenticated(),
    user: mockUser(),
  }),
}));

vi.mock('@/context/WebSocketContext', () => ({
  useWebSocket: () => ({
    send: mockSend,
    connectionState: mockConnectionState(),
  }),
}));

// Mock PricingSuggestions component to simplify tests
vi.mock('./PricingSuggestions', () => ({
  PricingSuggestions: ({ onSuggestionClick, disabled }: {
    onSuggestionClick: (amount: number) => void;
    disabled: boolean;
  }) => (
    <div data-testid="pricing-suggestions">
      <button
        onClick={() => onSuggestionClick(200)}
        disabled={disabled}
        data-testid="suggestion-btn"
      >
        AI Suggestion: $200
      </button>
    </div>
  ),
}));

// Helper to render with router
function renderWithRouter(ui: React.ReactElement) {
  return render(<MemoryRouter>{ui}</MemoryRouter>);
}

// Default props factory
function createDefaultProps(overrides = {}) {
  return {
    auctionId: 'auction-123',
    currentBid: 100,
    startingPrice: 50,
    isActive: true,
    bidError: null,
    onBidError: vi.fn(),
    currentWinnerId: null,
    ...overrides,
  };
}

describe('BiddingPanel', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockIsAuthenticated.mockReturnValue(true);
    mockUser.mockReturnValue({ userId: 'user-123', username: 'testuser' });
    mockConnectionState.mockReturnValue('connected');
  });

  describe('Unauthenticated user', () => {
    beforeEach(() => {
      mockIsAuthenticated.mockReturnValue(false);
      mockUser.mockReturnValue(null);
    });

    it('shows login prompt when not authenticated', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('You must be logged in to place bids.')).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /login to bid/i })).toBeInTheDocument();
    });

    it('links to login page', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      const loginLink = screen.getByRole('link', { name: /login to bid/i });
      expect(loginLink).toHaveAttribute('href', '/login');
    });

    it('does not show bidding controls', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.queryByText('+$5')).not.toBeInTheDocument();
      expect(screen.queryByText('+$10')).not.toBeInTheDocument();
      expect(screen.queryByText('+$50')).not.toBeInTheDocument();
    });
  });

  describe('Inactive auction', () => {
    it('shows ended message when auction is not active', () => {
      const props = createDefaultProps({ isActive: false, currentBid: 150 });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('This auction has ended.')).toBeInTheDocument();
      expect(screen.getByText('Winning bid: $150.00')).toBeInTheDocument();
    });

    it('shows winner message when current user won', () => {
      const props = createDefaultProps({
        isActive: false,
        currentBid: 200,
        currentWinnerId: 'user-123',
      });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('Congratulations! You won this auction!')).toBeInTheDocument();
      expect(screen.getByText('Winning bid: $200.00')).toBeInTheDocument();
    });

    it('shows no bids message when auction ended with no bids', () => {
      const props = createDefaultProps({
        isActive: false,
        currentBid: 0,
        currentWinnerId: null,
      });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('This auction has ended with no bids.')).toBeInTheDocument();
    });

    it('does not show bidding controls when inactive', () => {
      const props = createDefaultProps({ isActive: false });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.queryByText('+$5')).not.toBeInTheDocument();
    });
  });

  describe('Active auction - Winning indicator', () => {
    it('shows winning indicator when user is current highest bidder', () => {
      const props = createDefaultProps({ currentWinnerId: 'user-123' });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText("You're winning!")).toBeInTheDocument();
    });

    it('does not show winning indicator when user is not highest bidder', () => {
      const props = createDefaultProps({ currentWinnerId: 'other-user' });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.queryByText("You're winning!")).not.toBeInTheDocument();
    });

    it('does not show winning indicator when no bids placed', () => {
      const props = createDefaultProps({ currentWinnerId: null });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.queryByText("You're winning!")).not.toBeInTheDocument();
    });
  });

  describe('Quick bid buttons', () => {
    it('displays all three quick bid buttons', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('+$5')).toBeInTheDocument();
      expect(screen.getByText('+$10')).toBeInTheDocument();
      expect(screen.getByText('+$50')).toBeInTheDocument();
    });

    it('sends bid via WebSocket when quick bid button clicked', () => {
      const props = createDefaultProps({ currentBid: 100 });
      renderWithRouter(<BiddingPanel {...props} />);

      fireEvent.click(screen.getByText('+$5'));

      expect(mockSend).toHaveBeenCalledWith('/app/bid', {
        auctionId: 'auction-123',
        amount: 105,
      });
    });

    it('calculates bid amount based on current bid for quick bids', () => {
      const props = createDefaultProps({ currentBid: 200 });
      renderWithRouter(<BiddingPanel {...props} />);

      fireEvent.click(screen.getByText('+$10'));

      expect(mockSend).toHaveBeenCalledWith('/app/bid', {
        auctionId: 'auction-123',
        amount: 210,
      });
    });

    it('uses starting price when no bids exist for quick bids', () => {
      const props = createDefaultProps({ currentBid: 0, startingPrice: 50 });
      renderWithRouter(<BiddingPanel {...props} />);

      fireEvent.click(screen.getByText('+$5'));

      expect(mockSend).toHaveBeenCalledWith('/app/bid', {
        auctionId: 'auction-123',
        amount: 55,
      });
    });

    it('disables quick bid buttons when not connected', () => {
      mockConnectionState.mockReturnValue('disconnected');
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      const buttons = [
        screen.getByText('+$5'),
        screen.getByText('+$10'),
        screen.getByText('+$50'),
      ];

      buttons.forEach((button) => {
        expect(button).toBeDisabled();
      });
    });
  });

  describe('Custom bid input', () => {
    it('displays custom amount input field', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByRole('spinbutton')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /^bid$/i })).toBeInTheDocument();
    });

    it('shows minimum bid placeholder in input', () => {
      const props = createDefaultProps({ currentBid: 100 });
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      expect(input).toHaveAttribute('placeholder', '101.00');
    });

    it('submits custom bid amount', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      fireEvent.change(input, { target: { value: '150' } });
      fireEvent.click(screen.getByRole('button', { name: /^bid$/i }));

      expect(mockSend).toHaveBeenCalledWith('/app/bid', {
        auctionId: 'auction-123',
        amount: 150,
      });
    });

    it('shows error for invalid amount', () => {
      const onBidError = vi.fn();
      const props = createDefaultProps({ onBidError });
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      const form = input.closest('form')!;
      fireEvent.change(input, { target: { value: 'abc' } });
      fireEvent.submit(form);

      expect(onBidError).toHaveBeenCalledWith('Please enter a valid amount');
      expect(mockSend).not.toHaveBeenCalled();
    });

    it('shows error for bid below minimum', () => {
      const onBidError = vi.fn();
      const props = createDefaultProps({ currentBid: 100, onBidError });
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      const form = input.closest('form')!;
      fireEvent.change(input, { target: { value: '50' } });
      fireEvent.submit(form);

      expect(onBidError).toHaveBeenCalledWith('Bid must be at least $101.00');
      expect(mockSend).not.toHaveBeenCalled();
    });

    it('disables submit button when input is empty', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      const submitButton = screen.getByRole('button', { name: /^bid$/i });
      expect(submitButton).toBeDisabled();
    });

    it('enables submit button when input has value', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      fireEvent.change(input, { target: { value: '200' } });

      const submitButton = screen.getByRole('button', { name: /^bid$/i });
      expect(submitButton).not.toBeDisabled();
    });
  });

  describe('Error display', () => {
    it('shows bid error when present', () => {
      const props = createDefaultProps({ bidError: 'Your bid was too low' });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('Your bid was too low')).toBeInTheDocument();
    });

    it('does not show error section when no error', () => {
      const props = createDefaultProps({ bidError: null });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.queryByText(/your bid was/i)).not.toBeInTheDocument();
    });
  });

  describe('Minimum bid display', () => {
    it('shows minimum bid amount', () => {
      const props = createDefaultProps({ currentBid: 100 });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('Minimum bid: $101.00')).toBeInTheDocument();
    });

    it('shows starting price as minimum when no bids', () => {
      const props = createDefaultProps({ currentBid: 0, startingPrice: 50 });
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('Minimum bid: $50.00')).toBeInTheDocument();
    });
  });

  describe('Connection state', () => {
    it('shows connection warning when disconnected', () => {
      mockConnectionState.mockReturnValue('disconnected');
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByText('Connecting to server... Please wait.')).toBeInTheDocument();
    });

    it('does not show connection warning when connected', () => {
      mockConnectionState.mockReturnValue('connected');
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.queryByText(/connecting to server/i)).not.toBeInTheDocument();
    });

    it('does not send bid when disconnected', () => {
      mockConnectionState.mockReturnValue('disconnected');
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      // Quick bid buttons should be disabled
      const quickBidBtn = screen.getByText('+$5');
      fireEvent.click(quickBidBtn);

      expect(mockSend).not.toHaveBeenCalled();
    });
  });

  describe('AI Pricing Suggestions', () => {
    it('renders PricingSuggestions component', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByTestId('pricing-suggestions')).toBeInTheDocument();
    });

    it('handles suggestion click and submits bid', () => {
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      fireEvent.click(screen.getByTestId('suggestion-btn'));

      expect(mockSend).toHaveBeenCalledWith('/app/bid', {
        auctionId: 'auction-123',
        amount: 200,
      });
    });

    it('disables suggestions when not connected', () => {
      mockConnectionState.mockReturnValue('disconnected');
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      expect(screen.getByTestId('suggestion-btn')).toBeDisabled();
    });
  });

  describe('Bidding state', () => {
    it('shows loading state during submission', async () => {
      vi.useFakeTimers();
      const props = createDefaultProps();
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      const form = input.closest('form')!;
      fireEvent.change(input, { target: { value: '200' } });
      fireEvent.submit(form);

      // Check loading state is shown
      expect(screen.getByText('Bidding...')).toBeInTheDocument();

      // Wait for submitting state to clear
      await act(async () => {
        await vi.advanceTimersByTimeAsync(500);
      });

      // After timeout, button should be back to normal text
      // Note: Button will be disabled because customAmount is cleared,
      // so we check the button text specifically
      const submitButton = screen.getByRole('button', { name: /bid/i });
      expect(submitButton).toHaveTextContent('Bid');
      expect(submitButton).not.toHaveTextContent('Bidding...');

      vi.useRealTimers();
    });

    it('clears error when submitting new bid', () => {
      const onBidError = vi.fn();
      const props = createDefaultProps({ onBidError });
      renderWithRouter(<BiddingPanel {...props} />);

      const input = screen.getByRole('spinbutton');
      const form = input.closest('form')!;
      fireEvent.change(input, { target: { value: '200' } });
      fireEvent.submit(form);

      expect(onBidError).toHaveBeenCalledWith(null);
    });
  });
});
