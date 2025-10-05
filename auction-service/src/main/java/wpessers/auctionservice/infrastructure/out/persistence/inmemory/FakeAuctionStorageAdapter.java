package wpessers.auctionservice.infrastructure.out.persistence.inmemory;

import wpessers.auctionservice.application.port.out.AuctionStorage;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    public List<Auction> getActiveAuctions() {
        return auctions.values().stream()
            .filter((auction) -> auction.getStatus() == AuctionStatus.ACTIVE)
            .toList();
    }

    public Auction get(UUID auctionId) {
        return auctions.get(auctionId);
    }
}
