package wpessers.auctionservice.bid.domain.event;

import java.util.UUID;
import wpessers.auctionservice.shared.domain.Money;

public record BidRejectedEvent(UUID auctionId, UUID bidderId, Money amount,
                               RejectionReason reason) {

}
