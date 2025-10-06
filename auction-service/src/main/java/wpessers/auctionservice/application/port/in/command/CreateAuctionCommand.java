package wpessers.auctionservice.application.port.in.command;

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
