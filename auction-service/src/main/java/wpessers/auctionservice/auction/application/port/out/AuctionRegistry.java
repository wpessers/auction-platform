package wpessers.auctionservice.auction.application.port.out;

import wpessers.auctionservice.auction.domain.Auction;

public interface AuctionRegistry {

    void registerAuction(Auction auction);
}
