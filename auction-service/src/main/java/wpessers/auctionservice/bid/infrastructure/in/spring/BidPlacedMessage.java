package wpessers.auctionservice.bid.infrastructure.in.spring;

import java.util.UUID;

public record BidPlacedMessage(UUID bidderId, java.math.BigDecimal amount) {

}
