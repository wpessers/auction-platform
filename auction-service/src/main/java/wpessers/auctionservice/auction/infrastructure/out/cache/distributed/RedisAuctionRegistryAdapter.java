package wpessers.auctionservice.auction.infrastructure.out.cache.distributed;

import java.util.UUID;
import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.Auction;

@Component
@Profile("prod")
public class RedisAuctionRegistryAdapter implements AuctionRegistry {

    @Override
    public void register(Auction auction) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deregister(UUID auctionId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> T executeOnAuction(UUID auctionId, Function<Auction, T> action) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
