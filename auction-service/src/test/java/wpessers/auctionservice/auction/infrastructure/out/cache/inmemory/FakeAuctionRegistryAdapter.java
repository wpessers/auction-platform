package wpessers.auctionservice.auction.infrastructure.out.cache.inmemory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.Auction;

public class FakeAuctionRegistryAdapter implements AuctionRegistry {

    private final Set<UUID> registeredAuctions;

    public FakeAuctionRegistryAdapter() {
        registeredAuctions = new HashSet<>();
    }

    @Override
    public void register(Auction auction) {
        registeredAuctions.add(auction.getId());
    }

    @Override
    public void deregister(UUID auctionId) {
        registeredAuctions.remove(auctionId);
    }

    @Override
    public <T> T executeOnAuction(UUID auctionId, Function<Auction, T> action) {
        throw new UnsupportedOperationException("Not implemented in fake adapter");
    }

    public boolean isRegistered(UUID auctionId) {
        return registeredAuctions.contains(auctionId);
    }
}
