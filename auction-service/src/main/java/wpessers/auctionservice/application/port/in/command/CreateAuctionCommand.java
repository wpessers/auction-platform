package wpessers.auctionservice.application.port.in.command;

import java.time.Instant;
import java.util.Optional;

public record CreateAuctionCommand(
    String name,
    String description,
    Optional<Instant> endTime
) {

}
