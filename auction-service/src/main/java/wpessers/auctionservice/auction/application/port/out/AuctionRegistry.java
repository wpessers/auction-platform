package wpessers.auctionservice.auction.application.port.out;

import java.util.UUID;

public interface AuctionRegistry {

    void registerAuction(UUID auctionId);
}
