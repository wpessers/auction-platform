package wpessers.auctionservice.auction.infrastructure.out.persistence.inmemory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;

public class FakeAuctionStorageAdapter implements AuctionStorage {

    private final HashMap<UUID, Auction> auctions;

    public FakeAuctionStorageAdapter() {
        this.auctions = new HashMap<>();
    }

    @Override
    public void save(Auction auction) {
        auctions.put(auction.getId(), auction);
    }

    @Override
    public Optional<Auction> findById(UUID auctionId) {
        return Optional.ofNullable(auctions.get(auctionId));
    }

    @Override
    public Stream<Auction> getActiveAuctions() {
        return auctions.values().stream()
            .filter((auction) -> auction.getStatus() == AuctionStatus.ACTIVE);
    }

    public Auction get(UUID auctionId) {
        return auctions.get(auctionId);
    }
}
