package wpessers.auctionservice.auction.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateAuctionCommand(
    String name,
    String description,
    Instant startTime,
    Instant endTime,
    BigDecimal startingPrice
) {

}
