package wpessers.auctionservice.auction.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionResponse(
    UUID id,
    String name,
    String description,
    Instant startTime,
    Instant endTime,
    BigDecimal startingPrice
) {

}
