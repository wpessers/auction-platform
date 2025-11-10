package wpessers.auctionservice.auction.infrastructure.out.cache.distributed;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.Auction;

@Component
public class RedisAuctionRegistryAdapter implements AuctionRegistry {

    @Override
    public void registerAuction(Auction auction) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
