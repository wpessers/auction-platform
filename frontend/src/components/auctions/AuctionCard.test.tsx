import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { AuctionCard } from './AuctionCard';
import type { Auction } from '@/types/auction';

// Mock the useCountdown hook
vi.mock('@/hooks/useCountdown', () => ({
  useCountdown: vi.fn(),
}));

import { useCountdown } from '@/hooks/useCountdown';

const mockUseCountdown = useCountdown as ReturnType<typeof vi.fn>;

// Helper to render with router
function renderWithRouter(ui: React.ReactElement) {
  return render(<MemoryRouter>{ui}</MemoryRouter>);
}

// Factory function for creating test auctions
function createAuction(overrides: Partial<Auction> = {}): Auction {
  return {
    id: 'auction-123',
    name: 'Test Auction',
    description: 'A test auction description',
    startTime: '2024-01-15T10:00:00Z',
    endTime: '2024-01-15T22:00:00Z',
    startingPrice: 100,
    highestBid: null,
    currentWinnerId: null,
    status: 'ACTIVE',
    ...overrides,
  };
}

describe('AuctionCard', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Default mock return value
    mockUseCountdown.mockReturnValue({
      timeRemaining: '5 hours left',
      isUrgent: false,
      isExpired: false,
    });
  });

  describe('Display', () => {
    it('renders auction name', () => {
      const auction = createAuction({ name: 'Vintage Watch' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Vintage Watch')).toBeInTheDocument();
    });

    it('renders auction description', () => {
      const auction = createAuction({ description: 'A beautiful vintage timepiece' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('A beautiful vintage timepiece')).toBeInTheDocument();
    });

    it('displays starting price when no bids exist', () => {
      const auction = createAuction({
        startingPrice: 100,
        highestBid: null,
      });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Starting Price')).toBeInTheDocument();
      expect(screen.getByText('$100.00')).toBeInTheDocument();
    });

    it('displays current bid when bids exist', () => {
      const auction = createAuction({
        startingPrice: 100,
        highestBid: 150,
      });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Current Bid')).toBeInTheDocument();
      expect(screen.getByText('$150.00')).toBeInTheDocument();
    });

    it('formats large prices with commas', () => {
      const auction = createAuction({
        highestBid: 1234567.89,
      });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('$1,234,567.89')).toBeInTheDocument();
    });

    it('displays time remaining', () => {
      mockUseCountdown.mockReturnValue({
        timeRemaining: '2 days left',
        isUrgent: false,
        isExpired: false,
      });

      const auction = createAuction();
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Time Remaining')).toBeInTheDocument();
      expect(screen.getByText('2 days left')).toBeInTheDocument();
    });
  });

  describe('Status badges', () => {
    it('shows Active badge for active auction', () => {
      mockUseCountdown.mockReturnValue({
        timeRemaining: '5 hours left',
        isUrgent: false,
        isExpired: false,
      });

      const auction = createAuction({ status: 'ACTIVE' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Active')).toBeInTheDocument();
    });

    it('shows Scheduled badge for scheduled auction', () => {
      const auction = createAuction({ status: 'SCHEDULED' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Scheduled')).toBeInTheDocument();
    });

    it('shows Closed badge for closed auction', () => {
      const auction = createAuction({ status: 'CLOSED' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Closed')).toBeInTheDocument();
    });

    it('shows Closed badge when auction is expired', () => {
      mockUseCountdown.mockReturnValue({
        timeRemaining: 'Expired',
        isUrgent: false,
        isExpired: true,
      });

      const auction = createAuction({ status: 'ACTIVE' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Closed')).toBeInTheDocument();
    });

    it('shows Ending Soon badge when auction is urgent', () => {
      mockUseCountdown.mockReturnValue({
        timeRemaining: '4:32',
        isUrgent: true,
        isExpired: false,
      });

      const auction = createAuction({ status: 'ACTIVE' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('Ending Soon')).toBeInTheDocument();
    });
  });

  describe('Navigation', () => {
    it('links to auction detail page', () => {
      const auction = createAuction({ id: 'auction-456' });
      renderWithRouter(<AuctionCard auction={auction} />);

      const link = screen.getByRole('link');
      expect(link).toHaveAttribute('href', '/auctions/auction-456');
    });
  });

  describe('Styling', () => {
    it('applies urgent styling to time remaining when urgent', () => {
      mockUseCountdown.mockReturnValue({
        timeRemaining: '4:32',
        isUrgent: true,
        isExpired: false,
      });

      const auction = createAuction({ status: 'ACTIVE' });
      renderWithRouter(<AuctionCard auction={auction} />);

      const timeElement = screen.getByText('4:32');
      expect(timeElement).toHaveClass('text-error');
    });

    it('applies normal styling to time remaining when not urgent', () => {
      mockUseCountdown.mockReturnValue({
        timeRemaining: '5 hours left',
        isUrgent: false,
        isExpired: false,
      });

      const auction = createAuction({ status: 'ACTIVE' });
      renderWithRouter(<AuctionCard auction={auction} />);

      const timeElement = screen.getByText('5 hours left');
      expect(timeElement).toHaveClass('text-text-secondary');
    });
  });

  describe('Edge cases', () => {
    it('handles auction with zero starting price', () => {
      const auction = createAuction({
        startingPrice: 0,
        highestBid: null,
      });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(screen.getByText('$0.00')).toBeInTheDocument();
    });

    it('handles long auction names (truncated via CSS)', () => {
      const auction = createAuction({
        name: 'This is a very long auction name that should be truncated by CSS line-clamp',
      });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(
        screen.getByText(
          'This is a very long auction name that should be truncated by CSS line-clamp'
        )
      ).toBeInTheDocument();
    });

    it('handles long descriptions (truncated via CSS)', () => {
      const auction = createAuction({
        description:
          'This is a very long description that goes on and on and should be truncated by CSS line-clamp to prevent the card from becoming too tall and disrupting the grid layout.',
      });
      renderWithRouter(<AuctionCard auction={auction} />);

      const description = screen.getByText(/This is a very long description/);
      expect(description).toHaveClass('line-clamp-2');
    });

    it('passes endTime to useCountdown hook', () => {
      const auction = createAuction({ endTime: '2024-12-31T23:59:59Z' });
      renderWithRouter(<AuctionCard auction={auction} />);

      expect(mockUseCountdown).toHaveBeenCalledWith('2024-12-31T23:59:59Z');
    });
  });
});
