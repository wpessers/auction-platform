package wpessers.auctionservice.bid.domain.event;

import wpessers.auctionservice.shared.domain.Money;
import java.util.UUID;

public record BidRejectedEvent(UUID auctionId, UUID bidderId, Money amount) {

}
