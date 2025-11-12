package wpessers.auctionservice.bid.domain.event;

import wpessers.auctionservice.shared.domain.Money;
import java.util.UUID;

public record BidPlacedEvent(UUID auctionId, UUID bidderId, UUID previousBidderId, Money amount) {

}
