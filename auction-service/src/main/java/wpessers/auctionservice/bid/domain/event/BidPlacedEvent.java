package wpessers.auctionservice.bid.domain.event;

import java.util.UUID;
import wpessers.auctionservice.shared.domain.Money;

public record BidPlacedEvent(UUID auctionId, UUID bidderId, UUID previousBidderId, Money amount) {

}
