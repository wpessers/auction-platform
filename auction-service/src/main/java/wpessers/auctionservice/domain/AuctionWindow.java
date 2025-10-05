package wpessers.auctionservice.domain;

import java.time.Instant;
import wpessers.auctionservice.domain.exception.InvalidAuctionWindowException;

public record AuctionWindow(Instant startTime, Instant endTime) {

    public AuctionWindow {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidAuctionWindowException("End time must be after start time");
        }
    }
}
