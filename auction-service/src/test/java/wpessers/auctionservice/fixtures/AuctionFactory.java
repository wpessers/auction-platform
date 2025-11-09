package wpessers.auctionservice.fixtures;

import java.time.Instant;
import java.util.UUID;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.shared.domain.Money;

public class AuctionFactory {

    public static Auction closedAuctionWithBid(UUID bidderId, Money bidAmount) {
        Auction auction = new AuctionBuilder().build();
        auction.placeBid(bidderId, bidAmount, Instant.now());
        auction.end();
        return auction;
    }
}
