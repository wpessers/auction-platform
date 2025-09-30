package wpessers.auctionservice.application.port.out;

import wpessers.auctionservice.domain.Auction;

public interface AuctionStoragePort {

    void save(Auction auction);

}
