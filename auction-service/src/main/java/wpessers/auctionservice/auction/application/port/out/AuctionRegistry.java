package wpessers.auctionservice.auction.application.port.out;

import java.util.UUID;
import wpessers.auctionservice.auction.domain.Auction;

public interface AuctionRegistry {

    void register(Auction auction);

    void deregister(UUID auctionId);
}
