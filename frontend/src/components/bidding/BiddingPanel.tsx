import { useState, type FormEvent } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useWebSocket } from '@/context/WebSocketContext';
import { Link } from 'react-router-dom';
import { PricingSuggestions } from './PricingSuggestions';

interface BiddingPanelProps {
  auctionId: string;
  currentBid: number;
  startingPrice: number;
  isActive: boolean;
  bidError: string | null;
  onBidError: (error: string | null) => void;
  currentWinnerId: string | null;
}

export function BiddingPanel({
  auctionId,
  currentBid,
  startingPrice,
  isActive,
  bidError,
  onBidError,
  currentWinnerId,
}: BiddingPanelProps) {
  const { isAuthenticated, user } = useAuth();
  const { send, connectionState } = useWebSocket();
  const [customAmount, setCustomAmount] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const minimumBid = currentBid > 0 ? currentBid + 1 : startingPrice;
  const isConnected = connectionState === 'connected';
  const isWinning = currentWinnerId && user?.userId === currentWinnerId;

  const handleQuickBid = (increment: number) => {
    if (!isConnected || isSubmitting) return;

    const amount = currentBid > 0 ? currentBid + increment : startingPrice + increment;
    submitBid(amount);
  };

  const handleSuggestionClick = (amount: number) => {
    if (!isConnected || isSubmitting) return;
    submitBid(amount);
  };

  const handleCustomBid = (e: FormEvent) => {
    e.preventDefault();
    if (!isConnected || isSubmitting) return;

    const amount = parseFloat(customAmount);
    if (isNaN(amount) || amount <= 0) {
      onBidError('Please enter a valid amount');
      return;
    }
    if (amount < minimumBid) {
      onBidError(`Bid must be at least $${minimumBid.toFixed(2)}`);
      return;
    }

    submitBid(amount);
  };

  const submitBid = (amount: number) => {
    setIsSubmitting(true);
    onBidError(null);

    send('/app/bid', {
      auctionId,
      amount,
    });

    // Reset submitting state after a short delay
    // The actual result will come through WebSocket
    setTimeout(() => {
      setIsSubmitting(false);
      setCustomAmount('');
    }, 500);
  };

  if (!isAuthenticated) {
    return (
      <div className="rounded bg-card p-4 text-center">
        <p className="mb-3 text-text-secondary">
          You must be logged in to place bids.
        </p>
        <Link
          to="/login"
          className="inline-block min-h-[44px] rounded bg-accent px-4 py-3 font-medium text-background transition-colors hover:bg-accent-hover"
        >
          Login to Bid
        </Link>
      </div>
    );
  }

  if (!isActive) {
    // Determine if the current user won the auction
    const userWon = isAuthenticated && currentWinnerId && user?.userId === currentWinnerId;
    const hadBids = currentBid > 0;

    return (
      <div className="rounded bg-card p-4 text-center">
        {userWon ? (
          <>
            <div className="mb-2 flex justify-center">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-8 w-8 text-accent"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clipRule="evenodd"
                />
              </svg>
            </div>
            <p className="font-medium text-accent">
              Congratulations! You won this auction!
            </p>
            <p className="mt-1 text-sm text-text-secondary">
              Winning bid: ${currentBid.toFixed(2)}
            </p>
          </>
        ) : hadBids ? (
          <>
            <p className="text-text-secondary">
              This auction has ended.
            </p>
            <p className="mt-1 text-sm text-text-disabled">
              Winning bid: ${currentBid.toFixed(2)}
            </p>
          </>
        ) : (
          <p className="text-text-secondary">
            This auction has ended with no bids.
          </p>
        )}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Winning indicator */}
      {isWinning && (
        <div className="flex items-center gap-2 rounded bg-accent/20 p-3">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-5 w-5 text-accent"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fillRule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
              clipRule="evenodd"
            />
          </svg>
          <span className="font-medium text-accent">You're winning!</span>
        </div>
      )}

      {/* AI-Powered Pricing Suggestions */}
      <PricingSuggestions
        auctionId={auctionId}
        currentBid={currentBid}
        onSuggestionClick={handleSuggestionClick}
        disabled={!isConnected || isSubmitting}
      />

      {/* Divider */}
      <div className="flex items-center gap-3">
        <div className="h-px flex-1 bg-border"></div>
        <span className="text-xs text-text-disabled">or</span>
        <div className="h-px flex-1 bg-border"></div>
      </div>

      {/* Quick bid buttons */}
      <div>
        <p className="mb-2 text-sm text-text-disabled">Quick Bid</p>
        <div className="grid grid-cols-3 gap-2">
          {[5, 10, 50].map((increment) => (
            <button
              key={increment}
              onClick={() => handleQuickBid(increment)}
              disabled={!isConnected || isSubmitting}
              className="min-h-[44px] rounded bg-accent-muted px-3 py-3 text-sm font-medium text-accent transition-colors hover:bg-accent/30 disabled:cursor-not-allowed disabled:opacity-50"
            >
              +${increment}
            </button>
          ))}
        </div>
      </div>

      {/* Custom amount input */}
      <form onSubmit={handleCustomBid}>
        <p className="mb-2 text-sm text-text-disabled">Custom Amount</p>
        <div className="flex gap-2">
          <div className="relative flex-1">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-text-disabled">
              $
            </span>
            <input
              type="number"
              value={customAmount}
              onChange={(e) => setCustomAmount(e.target.value)}
              disabled={!isConnected || isSubmitting}
              min={minimumBid}
              step="0.01"
              placeholder={minimumBid.toFixed(2)}
              className="w-full rounded border border-border bg-card py-2 pl-7 pr-3 text-text-primary placeholder-text-disabled outline-none transition-colors focus:border-accent focus:ring-1 focus:ring-accent disabled:opacity-50"
            />
          </div>
          <button
            type="submit"
            disabled={!isConnected || isSubmitting || !customAmount}
            className="min-h-[44px] rounded bg-accent px-4 py-3 font-medium text-background transition-colors hover:bg-accent-hover disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isSubmitting ? 'Bidding...' : 'Bid'}
          </button>
        </div>
      </form>

      {/* Error display */}
      {bidError && (
        <div className="rounded bg-error/10 p-3 text-sm text-error">
          {bidError}
        </div>
      )}

      {/* Minimum bid info */}
      <p className="text-center text-xs text-text-disabled">
        Minimum bid: ${minimumBid.toFixed(2)}
      </p>

      {/* Connection warning */}
      {!isConnected && (
        <div className="rounded bg-warning/10 p-3 text-sm text-warning">
          Connecting to server... Please wait.
        </div>
      )}
    </div>
  );
}
