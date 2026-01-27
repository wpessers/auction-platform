import { Link } from 'react-router-dom';
import type { Auction } from '@/types/auction';
import { useCountdown } from '@/hooks/useCountdown';

interface AuctionCardProps {
  auction: Auction;
}

export function AuctionCard({ auction }: AuctionCardProps) {
  const { timeRemaining, isUrgent, isExpired } = useCountdown(auction.endTime);

  const currentPrice = auction.highestBid ?? auction.startingPrice;

  const getStatusBadge = () => {
    if (auction.status === 'CLOSED' || isExpired) {
      return (
        <span className="rounded bg-text-disabled px-2 py-0.5 text-xs font-medium text-text-primary">
          Closed
        </span>
      );
    }
    if (auction.status === 'SCHEDULED') {
      return (
        <span className="rounded bg-info/20 px-2 py-0.5 text-xs font-medium text-info">
          Scheduled
        </span>
      );
    }
    if (isUrgent) {
      return (
        <span className="rounded bg-error/20 px-2 py-0.5 text-xs font-medium text-error">
          Ending Soon
        </span>
      );
    }
    return (
      <span className="rounded bg-accent-muted px-2 py-0.5 text-xs font-medium text-accent">
        Active
      </span>
    );
  };

  return (
    <Link
      to={`/auctions/${auction.id}`}
      className="block rounded-lg border border-border bg-card p-4 transition-colors duration-fast hover:border-accent/50"
    >
      <div className="flex items-start justify-between gap-2">
        <h3 className="line-clamp-1 text-lg font-semibold text-text-primary">
          {auction.name}
        </h3>
        {getStatusBadge()}
      </div>

      <p className="mt-2 line-clamp-2 text-sm text-text-secondary">
        {auction.description}
      </p>

      <div className="mt-4 flex items-end justify-between">
        <div>
          <p className="text-xs text-text-disabled">
            {auction.highestBid ? 'Current Bid' : 'Starting Price'}
          </p>
          <p className="text-xl font-bold text-accent">
            ${currentPrice.toLocaleString('en-US', { minimumFractionDigits: 2 })}
          </p>
        </div>

        <div className="text-right">
          <p className="text-xs text-text-disabled">Time Remaining</p>
          <p
            className={`text-sm font-medium ${
              isUrgent ? 'text-error' : 'text-text-secondary'
            }`}
          >
            {timeRemaining}
          </p>
        </div>
      </div>
    </Link>
  );
}
