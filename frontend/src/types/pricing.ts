export type MarketHeat = 'LOW' | 'MEDIUM' | 'HIGH' | 'VERY_HIGH';
export type PriceTrend = 'STABLE' | 'RISING' | 'RISING_FAST';

export interface BidSuggestion {
  conservativeBid: number;
  moderateBid: number;
  aggressiveBid: number;
  confidence: number;
  reasoning: string;
}

export interface AuctionAnalysis {
  marketHeat: MarketHeat;
  estimatedFinalPrice: number;
  priceTrend: PriceTrend;
  recommendation: string;
  activitySummary: string;
}
