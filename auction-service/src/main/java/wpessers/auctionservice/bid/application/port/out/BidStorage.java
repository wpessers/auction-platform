package wpessers.auctionservice.bid.application.port.out;

import java.util.List;
import java.util.UUID;
import wpessers.auctionservice.bid.domain.Bid;

public interface BidStorage {

    void save(Bid bid);

    List<Bid> findByAuctionId(UUID auctionId);
}
