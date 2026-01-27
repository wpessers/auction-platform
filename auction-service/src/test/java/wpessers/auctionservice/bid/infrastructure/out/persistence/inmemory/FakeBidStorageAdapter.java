package wpessers.auctionservice.bid.infrastructure.out.persistence.inmemory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import wpessers.auctionservice.bid.application.port.out.BidStorage;
import wpessers.auctionservice.bid.domain.Bid;

public class FakeBidStorageAdapter implements BidStorage {

    private final List<Bid> bids;

    public FakeBidStorageAdapter() {
        this.bids = new ArrayList<>();
    }

    @Override
    public void save(Bid bid) {
        bids.add(bid);
    }

    @Override
    public List<Bid> findByAuctionId(UUID auctionId) {
        return bids.stream()
            .filter(bid -> bid.auctionId().equals(auctionId))
            .sorted(Comparator.comparing(Bid::timestamp).reversed())
            .toList();
    }

    public List<Bid> getBids() {
        return bids;
    }
}
