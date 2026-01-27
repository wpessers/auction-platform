import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { auctionsApi } from '@/api/auctions';
import type { Auction } from '@/types/auction';
import { useCountdown } from '@/hooks/useCountdown';
import { ApiError } from '@/api/client';
import { format } from 'date-fns';

export function AuctionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [auction, setAuction] = useState<Auction | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAuction = async () => {
      if (!id) return;

      try {
        const data = await auctionsApi.getById(id);
        setAuction(data);
        setError(null);
      } catch (err) {
        if (err instanceof ApiError) {
          if (err.status === 404) {
            setError('Auction not found');
          } else {
            setError(err.message);
          }
        } else {
          setError('Failed to load auction. Please try again.');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchAuction();
  }, [id]);

  if (isLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <div className="text-text-secondary">Loading auction...</div>
      </div>
    );
  }

  if (error || !auction) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center">
        <p className="mb-4 text-error">{error || 'Auction not found'}</p>
        <Link
          to="/auctions"
          className="text-accent transition-colors duration-fast hover:text-accent-hover"
        >
          Back to Auctions
        </Link>
      </div>
    );
  }

  return <AuctionDetailContent auction={auction} />;
}

interface AuctionDetailContentProps {
  auction: Auction;
}

function AuctionDetailContent({ auction }: AuctionDetailContentProps) {
  const { timeRemaining, isUrgent, isExpired } = useCountdown(auction.endTime);
  const currentPrice = auction.highestBid ?? auction.startingPrice;

  const getStatusDisplay = () => {
    if (auction.status === 'CLOSED' || isExpired) {
      return {
        label: 'Closed',
        className: 'bg-text-disabled text-text-primary',
      };
    }
    if (auction.status === 'SCHEDULED') {
      return {
        label: 'Scheduled',
        className: 'bg-info/20 text-info',
      };
    }
    if (isUrgent) {
      return {
        label: 'Ending Soon',
        className: 'bg-error/20 text-error',
      };
    }
    return {
      label: 'Active',
      className: 'bg-accent-muted text-accent',
    };
  };

  const status = getStatusDisplay();

  return (
    <div>
      {/* Back navigation */}
      <Link
        to="/auctions"
        className="mb-6 inline-flex items-center gap-1 text-sm text-text-secondary transition-colors duration-fast hover:text-accent"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-4 w-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M15 19l-7-7 7-7"
          />
        </svg>
        Back to Auctions
      </Link>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Main content */}
        <div className="lg:col-span-2">
          <div className="rounded-lg border border-border bg-surface p-6">
            <div className="flex items-start justify-between gap-4">
              <h1 className="text-2xl font-bold text-text-primary">
                {auction.name}
              </h1>
              <span
                className={`rounded px-3 py-1 text-sm font-medium ${status.className}`}
              >
                {status.label}
              </span>
            </div>

            <p className="mt-4 whitespace-pre-wrap text-text-secondary">
              {auction.description}
            </p>

            <div className="mt-6 grid gap-4 border-t border-border pt-6 sm:grid-cols-2">
              <div>
                <p className="text-sm text-text-disabled">Start Time</p>
                <p className="text-text-primary">
                  {format(new Date(auction.startTime), 'PPpp')}
                </p>
              </div>
              <div>
                <p className="text-sm text-text-disabled">End Time</p>
                <p className="text-text-primary">
                  {format(new Date(auction.endTime), 'PPpp')}
                </p>
              </div>
              <div>
                <p className="text-sm text-text-disabled">Starting Price</p>
                <p className="text-text-primary">
                  ${auction.startingPrice.toLocaleString('en-US', { minimumFractionDigits: 2 })}
                </p>
              </div>
              <div>
                <p className="text-sm text-text-disabled">Auction ID</p>
                <p className="font-mono text-sm text-text-secondary">
                  {auction.id}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar - Bidding panel placeholder */}
        <div className="lg:col-span-1">
          <div className="sticky top-4 rounded-lg border border-border bg-surface p-6">
            {/* Time remaining */}
            <div className="mb-6">
              <p className="text-sm text-text-disabled">Time Remaining</p>
              <p
                className={`text-2xl font-bold ${
                  isUrgent ? 'text-error' : 'text-text-primary'
                }`}
              >
                {timeRemaining}
              </p>
            </div>

            {/* Current price */}
            <div className="mb-6">
              <p className="text-sm text-text-disabled">
                {auction.highestBid ? 'Current Bid' : 'Starting Price'}
              </p>
              <p className="text-3xl font-bold text-accent">
                ${currentPrice.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
              {auction.highestBid && auction.currentWinnerId && (
                <p className="mt-1 text-sm text-text-secondary">
                  Leading bidder: {auction.currentWinnerId.slice(0, 8)}...
                </p>
              )}
            </div>

            {/* Bidding panel placeholder (Phase 4) */}
            {auction.status === 'ACTIVE' && !isExpired ? (
              <div className="rounded border border-dashed border-border p-4">
                <p className="text-center text-sm text-text-disabled">
                  Bidding panel coming in Phase 4
                </p>
              </div>
            ) : (
              <div className="rounded bg-card p-4 text-center">
                <p className="text-text-secondary">
                  {auction.status === 'SCHEDULED'
                    ? 'This auction has not started yet.'
                    : 'This auction has ended.'}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
