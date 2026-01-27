import { api } from './client';
import type { BidSuggestion, AuctionAnalysis } from '@/types/pricing';

export const pricingApi = {
  getSuggestions: (auctionId: string): Promise<BidSuggestion> =>
    api.get(`/api/pricing/auctions/${auctionId}/suggestions`),

  getAnalysis: (auctionId: string): Promise<AuctionAnalysis> =>
    api.get(`/api/pricing/auctions/${auctionId}/analysis`),
};
