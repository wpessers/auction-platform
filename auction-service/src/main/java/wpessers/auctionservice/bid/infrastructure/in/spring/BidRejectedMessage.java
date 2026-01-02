package wpessers.auctionservice.bid.infrastructure.in.spring;

import java.util.UUID;

public record BidRejectedMessage(UUID bidderId, String reason) {

}
