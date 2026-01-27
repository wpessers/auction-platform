import { useState, useEffect, useMemo } from 'react';
import { auctionsApi } from '@/api/auctions';
import type { Auction } from '@/types/auction';
import { AuctionCard } from '@/components/auctions/AuctionCard';
import { ApiError } from '@/api/client';

export function AuctionsPage() {
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchAuctions = async () => {
      try {
        const data = await auctionsApi.getActive();
        setAuctions(data);
        setError(null);
      } catch (err) {
        if (err instanceof ApiError) {
          setError(err.message);
        } else {
          setError('Failed to load auctions. Please try again.');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchAuctions();
  }, []);

  const filteredAuctions = useMemo(() => {
    if (!searchQuery.trim()) return auctions;

    const query = searchQuery.toLowerCase();
    return auctions.filter((auction) =>
      auction.name.toLowerCase().includes(query)
    );
  }, [auctions, searchQuery]);

  const handleClearSearch = () => {
    setSearchQuery('');
  };

  if (isLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <div className="text-text-secondary">Loading auctions...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center">
        <p className="mb-4 text-error">{error}</p>
        <button
          onClick={() => window.location.reload()}
          className="rounded bg-accent px-4 py-2 font-medium text-background transition-colors duration-fast hover:bg-accent-hover"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold text-text-primary">Active Auctions</h1>

        <div className="relative">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search auctions..."
            className="w-full rounded border border-border bg-card px-3 py-2 pr-10 text-text-primary placeholder-text-disabled outline-none transition-colors duration-fast focus:border-accent focus:ring-1 focus:ring-accent sm:w-64"
          />
          {searchQuery && (
            <button
              onClick={handleClearSearch}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-text-disabled transition-colors hover:text-text-secondary"
              aria-label="Clear search"
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
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          )}
        </div>
      </div>

      {auctions.length === 0 ? (
        <div className="flex min-h-[40vh] flex-col items-center justify-center rounded-lg border border-border bg-surface p-8">
          <p className="mb-2 text-lg text-text-primary">No Active Auctions</p>
          <p className="text-text-secondary">
            Check back later for new auctions.
          </p>
        </div>
      ) : filteredAuctions.length === 0 ? (
        <div className="flex min-h-[40vh] flex-col items-center justify-center rounded-lg border border-border bg-surface p-8">
          <p className="mb-2 text-lg text-text-primary">No Results Found</p>
          <p className="mb-4 text-text-secondary">
            No auctions match "{searchQuery}"
          </p>
          <button
            onClick={handleClearSearch}
            className="text-accent transition-colors duration-fast hover:text-accent-hover"
          >
            Clear Search
          </button>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {filteredAuctions.map((auction) => (
            <AuctionCard key={auction.id} auction={auction} />
          ))}
        </div>
      )}
    </div>
  );
}
