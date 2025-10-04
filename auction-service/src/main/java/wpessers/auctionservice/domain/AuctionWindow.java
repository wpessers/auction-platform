package wpessers.auctionservice.domain;

import wpessers.auctionservice.domain.exception.InvalidAuctionWindowException;
import java.time.Instant;

public record AuctionWindow(Instant startTime, Instant endTime) {

    public AuctionWindow {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidAuctionWindowException("End time must be after start time");
        }
    }
}
