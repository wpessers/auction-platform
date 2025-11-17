package wpessers.auctionservice.bid.domain;

import java.time.Instant;
import java.util.UUID;
import wpessers.auctionservice.shared.domain.Money;

public record Bid(
    UUID auctionId,
    UUID bidderId,
    Money amount,
    Instant timestamp
) {

}