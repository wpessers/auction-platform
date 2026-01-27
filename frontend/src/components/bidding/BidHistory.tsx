import { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { auctionsApi } from '@/api/auctions';
import type { BidHistoryItem } from '@/types/auction';
import { useAuth } from '@/context/AuthContext';

interface BidHistoryProps {
  auctionId: string;
  currentWinnerId: string | null;
}

export function BidHistory({ auctionId, currentWinnerId }: BidHistoryProps) {
  const [bids, setBids] = useState<BidHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  useEffect(() => {
    const fetchBidHistory = async () => {
      try {
        const data = await auctionsApi.getBidHistory(auctionId);
        setBids(data);
        setError(null);
      } catch {
        setError('Failed to load bid history');
      } finally {
        setIsLoading(false);
      }
    };

    fetchBidHistory();
  }, [auctionId]);

  if (isLoading) {
    return (
      <div className="rounded-lg border border-border bg-surface p-6">
        <h3 className="mb-4 text-lg font-semibold text-text-primary">
          Bid History
        </h3>
        <div className="animate-pulse space-y-3">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-12 rounded bg-bg-secondary" />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-border bg-surface p-6">
        <h3 className="mb-4 text-lg font-semibold text-text-primary">
          Bid History
        </h3>
        <p className="text-sm text-error">{error}</p>
      </div>
    );
  }

  if (bids.length === 0) {
    return (
      <div className="rounded-lg border border-border bg-surface p-6">
        <h3 className="mb-4 text-lg font-semibold text-text-primary">
          Bid History
        </h3>
        <p className="text-sm text-text-secondary">No bids placed yet.</p>
      </div>
    );
  }

  return (
    <div className="rounded-lg border border-border bg-surface p-6">
      <h3 className="mb-4 text-lg font-semibold text-text-primary">
        Bid History
      </h3>
      <div className="space-y-3">
        {bids.map((bid, index) => {
          const isCurrentWinner = bid.bidderId === currentWinnerId;
          const isCurrentUser = bid.bidderId === user?.userId;

          return (
            <div
              key={`${bid.bidderId}-${bid.timestamp}`}
              className={`flex items-center justify-between rounded-lg p-3 ${
                index === 0
                  ? 'border border-accent/30 bg-accent-muted/30'
                  : 'bg-bg-secondary'
              }`}
            >
              <div className="flex items-center gap-3">
                <div
                  className={`flex h-8 w-8 items-center justify-center rounded-full text-sm font-medium ${
                    index === 0
                      ? 'bg-accent text-white'
                      : 'bg-border text-text-secondary'
                  }`}
                >
                  {index + 1}
                </div>
                <div>
                  <p className="text-sm font-medium text-text-primary">
                    {isCurrentUser ? (
                      <span className="text-accent">You</span>
                    ) : (
                      <span className="font-mono">
                        {bid.bidderId.slice(0, 8)}...
                      </span>
                    )}
                    {isCurrentWinner && index === 0 && (
                      <span className="ml-2 rounded bg-accent/20 px-2 py-0.5 text-xs text-accent">
                        Winning
                      </span>
                    )}
                  </p>
                  <p className="text-xs text-text-disabled">
                    {format(new Date(bid.timestamp), 'MMM d, yyyy h:mm:ss a')}
                  </p>
                </div>
              </div>
              <p
                className={`text-lg font-semibold ${
                  index === 0 ? 'text-accent' : 'text-text-primary'
                }`}
              >
                ${bid.amount.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>
          );
        })}
      </div>
    </div>
  );
}
