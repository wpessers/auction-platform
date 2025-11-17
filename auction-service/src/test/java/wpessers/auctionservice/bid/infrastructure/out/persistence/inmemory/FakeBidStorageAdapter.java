package wpessers.auctionservice.bid.infrastructure.out.persistence.inmemory;

import java.util.ArrayList;
import java.util.List;
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

    public List<Bid> getBids() {
        return bids;
    }
}
