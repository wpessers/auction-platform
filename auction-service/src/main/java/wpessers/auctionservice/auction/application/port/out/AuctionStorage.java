package wpessers.auctionservice.auction.application.port.out;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import wpessers.auctionservice.auction.domain.Auction;

public interface AuctionStorage {

    void save(Auction auction);

    Optional<Auction> findById(UUID auctionId);

    Stream<Auction> getActiveAuctions();
}
