import { useState, useEffect } from 'react';
import { pricingApi } from '@/api/pricing';
import type { BidSuggestion, AuctionAnalysis } from '@/types/pricing';

interface PricingSuggestionsProps {
  auctionId: string;
  currentBid: number;
  onSuggestionClick: (amount: number) => void;
  disabled?: boolean;
}

export function PricingSuggestions({
  auctionId,
  currentBid,
  onSuggestionClick,
  disabled = false,
}: PricingSuggestionsProps) {
  const [suggestions, setSuggestions] = useState<BidSuggestion | null>(null);
  const [analysis, setAnalysis] = useState<AuctionAnalysis | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;

    const fetchPricingData = async () => {
      try {
        setLoading(true);
        setError(null);

        const [suggestionsData, analysisData] = await Promise.all([
          pricingApi.getSuggestions(auctionId),
          pricingApi.getAnalysis(auctionId),
        ]);

        if (mounted) {
          setSuggestions(suggestionsData);
          setAnalysis(analysisData);
        }
      } catch (err) {
        if (mounted) {
          setError('Unable to load pricing suggestions');
          console.error('Error fetching pricing data:', err);
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    fetchPricingData();

    return () => {
      mounted = false;
    };
  }, [auctionId, currentBid]);

  if (loading) {
    return (
      <div className="animate-pulse rounded bg-card p-4">
        <div className="mb-3 h-4 w-32 rounded bg-border"></div>
        <div className="space-y-2">
          <div className="h-10 rounded bg-border"></div>
          <div className="h-10 rounded bg-border"></div>
          <div className="h-10 rounded bg-border"></div>
        </div>
      </div>
    );
  }

  if (error || !suggestions) {
    return null; // Silently fail - bidding panel still works without suggestions
  }

  const getMarketHeatColor = (heat: string) => {
    switch (heat) {
      case 'LOW':
        return 'text-text-secondary';
      case 'MEDIUM':
        return 'text-info';
      case 'HIGH':
        return 'text-warning';
      case 'VERY_HIGH':
        return 'text-error';
      default:
        return 'text-text-secondary';
    }
  };

  const getTrendIcon = (trend: string) => {
    switch (trend) {
      case 'STABLE':
        return '→';
      case 'RISING':
        return '↗';
      case 'RISING_FAST':
        return '⇈';
      default:
        return '→';
    }
  };

  return (
    <div className="space-y-4">
      {/* AI Suggested Bids */}
      <div>
        <p className="mb-2 flex items-center gap-2 text-sm text-text-disabled">
          <span className="inline-block h-2 w-2 animate-pulse rounded-full bg-accent"></span>
          AI Suggested Bids
        </p>
        <div className="grid grid-cols-3 gap-2">
          <button
            onClick={() => onSuggestionClick(suggestions.conservativeBid)}
            disabled={disabled}
            className="group rounded border border-border bg-card p-2 text-center transition-colors hover:border-accent hover:bg-accent-muted disabled:cursor-not-allowed disabled:opacity-50"
          >
            <span className="block text-xs text-text-disabled">Safe</span>
            <span className="block font-medium text-accent">
              ${suggestions.conservativeBid.toFixed(2)}
            </span>
          </button>
          <button
            onClick={() => onSuggestionClick(suggestions.moderateBid)}
            disabled={disabled}
            className="group rounded border border-accent bg-accent-muted p-2 text-center transition-colors hover:bg-accent/30 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <span className="block text-xs text-text-disabled">Balanced</span>
            <span className="block font-medium text-accent">
              ${suggestions.moderateBid.toFixed(2)}
            </span>
          </button>
          <button
            onClick={() => onSuggestionClick(suggestions.aggressiveBid)}
            disabled={disabled}
            className="group rounded border border-border bg-card p-2 text-center transition-colors hover:border-accent hover:bg-accent-muted disabled:cursor-not-allowed disabled:opacity-50"
          >
            <span className="block text-xs text-text-disabled">Bold</span>
            <span className="block font-medium text-accent">
              ${suggestions.aggressiveBid.toFixed(2)}
            </span>
          </button>
        </div>
        {suggestions.confidence > 0 && (
          <p className="mt-2 text-xs text-text-disabled">
            {suggestions.reasoning}
          </p>
        )}
      </div>

      {/* Market Analysis */}
      {analysis && (
        <div className="rounded border border-border bg-card/50 p-3">
          <p className="mb-2 text-sm font-medium text-text-primary">Market Analysis</p>
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span className="text-text-disabled">Heat: </span>
              <span className={getMarketHeatColor(analysis.marketHeat)}>
                {analysis.marketHeat.replace('_', ' ')}
              </span>
            </div>
            <div>
              <span className="text-text-disabled">Trend: </span>
              <span className="text-text-primary">
                {getTrendIcon(analysis.priceTrend)} {analysis.priceTrend.replace('_', ' ')}
              </span>
            </div>
            <div className="col-span-2">
              <span className="text-text-disabled">Est. Final: </span>
              <span className="font-medium text-accent">
                ${analysis.estimatedFinalPrice.toFixed(2)}
              </span>
            </div>
          </div>
          <p className="mt-2 text-xs text-text-secondary">
            {analysis.recommendation}
          </p>
        </div>
      )}
    </div>
  );
}
