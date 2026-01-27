export type AuctionStatus = 'SCHEDULED' | 'ACTIVE' | 'CLOSED';

export interface Auction {
  id: string;
  name: string;
  description: string;
  startTime: string; // ISO-8601
  endTime: string; // ISO-8601
  startingPrice: number;
  highestBid: number | null;
  currentWinnerId: string | null;
  status: AuctionStatus;
}

export interface CreateAuctionRequest {
  name: string;
  description: string;
  startTime: string; // ISO-8601
  endTime: string; // ISO-8601
  startingPrice: number;
}
