package wpessers.auctionservice.bid.infrastructure.out.persistence.inmemory;

import wpessers.auctionservice.bid.application.port.out.BidStorage;
import wpessers.auctionservice.bid.domain.Bid;
import java.util.ArrayList;
import java.util.List;

public class FakeBidStorageAdapter implements BidStorage {

    private List<Bid> bids;

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
