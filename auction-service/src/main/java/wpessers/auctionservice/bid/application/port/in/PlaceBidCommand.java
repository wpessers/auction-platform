package wpessers.auctionservice.bid.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record PlaceBidCommand(UUID bidderId, UUID auctionId, BigDecimal amount) {

}
