package wpessers.auctionservice.application.port.out;

import wpessers.auctionservice.domain.Auction;
import java.util.List;

public interface AuctionStorage {

    void save(Auction auction);

    List<Auction> getActiveAuctions();
}
