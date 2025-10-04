package wpessers.auctionservice.application.port.out;

import wpessers.auctionservice.domain.Auction;

public interface AuctionStorage {

    void save(Auction auction);
}
