package wpessers.auctionservice.bid.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BidResponse(
    UUID bidderId,
    BigDecimal amount,
    Instant timestamp
) {

}
