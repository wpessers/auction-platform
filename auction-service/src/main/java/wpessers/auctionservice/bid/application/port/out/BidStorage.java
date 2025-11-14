package wpessers.auctionservice.bid.application.port.out;

import wpessers.auctionservice.bid.domain.Bid;

public interface BidStorage {

    void save(Bid bid);
}
