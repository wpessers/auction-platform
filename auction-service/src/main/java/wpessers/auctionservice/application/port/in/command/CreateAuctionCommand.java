package wpessers.auctionservice.application.port.in.command;

import java.time.Instant;

public record CreateAuctionCommand(
    String name,
    String description,
    Instant startTime,
    Instant endTime,
    double startingPrice
) {

}
