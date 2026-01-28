import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { auctionsApi } from '@/api/auctions';
import type { Auction } from '@/types/auction';
import { AuctionCard } from '@/components/auctions/AuctionCard';
import { CreateAuctionModal } from '@/components/auctions/CreateAuctionModal';
import { FAB } from '@/components/ui/FAB';
import { useAuth } from '@/context/AuthContext';
import { useWebSocket } from '@/context/WebSocketContext';
import { ApiError } from '@/api/client';
import { AuctionCardSkeleton } from '@/components/ui/Skeleton';
import { useDebounce } from '@/hooks/useDebounce';

interface BidPlacedMessage {
  bidderId: string;
  amount: number;
}

export function AuctionsPage() {
  const { isAuthenticated } = useAuth();
  const { subscribe, connectionState } = useWebSocket();
  const [searchParams, setSearchParams] = useSearchParams();
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const subscriptionsRef = useRef<(() => void)[]>([]);

  // Get search query from URL, default to empty string
  const searchQuery = searchParams.get('search') || '';
  const debouncedSearchQuery = useDebounce(searchQuery, 300);

  // Update URL when search changes
  const setSearchQuery = useCallback(
    (query: string) => {
      if (query) {
        setSearchParams({ search: query }, { replace: true });
      } else {
        setSearchParams({}, { replace: true });
      }
    },
    [setSearchParams]
  );

  const fetchAuctions = useCallback(async () => {
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
  }, []);

  useEffect(() => {
    fetchAuctions();
  }, [fetchAuctions]);

  // Get a stable list of auction IDs to track which auctions need subscriptions
  const auctionIds = useMemo(() => auctions.map(a => a.id), [auctions]);
  const auctionIdsKey = auctionIds.join(',');

  // Subscribe to bid updates for all displayed auctions
  useEffect(() => {
    // Only subscribe when connected
    if (connectionState !== 'connected' || auctionIds.length === 0) {
      return;
    }

    // Clean up previous subscriptions
    subscriptionsRef.current.forEach(unsubscribe => unsubscribe());
    subscriptionsRef.current = [];

    // Subscribe to each auction's bid updates
    auctionIds.forEach(auctionId => {
      const handleBidPlaced = (message: unknown) => {
        const bid = message as BidPlacedMessage;
        setAuctions(prevAuctions =>
          prevAuctions.map(a =>
            a.id === auctionId
              ? { ...a, highestBid: bid.amount, currentWinnerId: bid.bidderId }
              : a
          )
        );
      };

      const unsubscribe = subscribe(`/topic/auctions/${auctionId}`, handleBidPlaced);
      subscriptionsRef.current.push(unsubscribe);
    });

    // Cleanup on unmount or when auctions change
    return () => {
      subscriptionsRef.current.forEach(unsubscribe => unsubscribe());
      subscriptionsRef.current = [];
    };
  }, [auctionIdsKey, connectionState, subscribe, auctionIds]);

  const filteredAuctions = useMemo(() => {
    if (!debouncedSearchQuery.trim()) return auctions;

    const query = debouncedSearchQuery.toLowerCase();
    return auctions.filter((auction) =>
      auction.name.toLowerCase().includes(query)
    );
  }, [auctions, debouncedSearchQuery]);

  const handleClearSearch = useCallback(() => {
    setSearchQuery('');
  }, [setSearchQuery]);

  const handleCreateSuccess = () => {
    // Refresh the auction list
    fetchAuctions();
  };

  if (isLoading) {
    return (
      <div>
        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold text-text-primary">Active Auctions</h1>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <AuctionCardSkeleton key={i} />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center">
        <div className="mb-4 text-4xl">⚠️</div>
        <h2 className="mb-2 text-xl font-bold text-text-primary">Failed to Load Auctions</h2>
        <p className="mb-6 text-text-secondary">{error}</p>
        <button
          onClick={() => {
            setIsLoading(true);
            setError(null);
            fetchAuctions();
          }}
          className="min-h-[44px] rounded bg-accent px-4 py-3 font-medium text-background transition-colors duration-fast hover:bg-accent-hover"
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
          {filteredAuctions.map((auction, index) => (
            <div
              key={auction.id}
              className="animate-fade-in"
              style={{ animationDelay: `${index * 50}ms` }}
            >
              <AuctionCard auction={auction} />
            </div>
          ))}
        </div>
      )}

      {/* FAB for creating auctions (only for authenticated users) */}
      {isAuthenticated && (
        <FAB onClick={() => setIsModalOpen(true)} label="Create Auction" />
      )}

      {/* Create Auction Modal */}
      <CreateAuctionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={handleCreateSuccess}
      />
    </div>
  );
}
