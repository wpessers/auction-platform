package wpessers.auctionservice.auction.infrastructure.out.cache.inmemory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.exception.AuctionNotFoundException;

@Component
public class InMemoryAuctionRegistry implements AuctionRegistry {

    private final ConcurrentMap<UUID, AuctionHolder> auctions;

    public InMemoryAuctionRegistry() {
        this.auctions = new ConcurrentHashMap<>();
    }

    @Override
    public void register(Auction auction) {
        auctions.put(auction.getId(), new AuctionHolder(auction));
    }

    @Override
    public void deregister(UUID auctionId) {
        auctions.remove(auctionId);
    }

    @Override
    public <T> T executeOnAuction(UUID auctionId, Function<Auction, T> action) {
        AuctionHolder holder = auctions.get(auctionId);

        if (holder == null) {
            throw new AuctionNotFoundException(
                String.format("Auction with id %s not found", auctionId));
        }

        holder.lock.writeLock().lock();
        try {
            return action.apply(holder.auction);
        } finally {
            holder.lock.writeLock().unlock();
        }
    }

    private static class AuctionHolder {

        private final Auction auction;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        AuctionHolder(Auction auction) {
            this.auction = auction;
        }
    }
}
