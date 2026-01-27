import { api } from './client';
import type { Auction, CreateAuctionRequest, BidHistoryItem } from '@/types/auction';

export const auctionsApi = {
  getActive: (): Promise<Auction[]> => api.get('/api/auctions/active'),

  getById: (id: string): Promise<Auction> => api.get(`/api/auctions/${id}`),

  create: (data: CreateAuctionRequest): Promise<string> =>
    api.post('/api/auctions', data),

  getBidHistory: (auctionId: string): Promise<BidHistoryItem[]> =>
    api.get(`/api/auctions/${auctionId}/bids`),
};
