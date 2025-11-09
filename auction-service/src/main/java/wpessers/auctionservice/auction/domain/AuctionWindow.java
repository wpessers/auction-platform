package wpessers.auctionservice.auction.domain;

import java.time.Instant;
import wpessers.auctionservice.auction.domain.exception.InvalidAuctionWindowException;

public record AuctionWindow(Instant startTime, Instant endTime) {

    public AuctionWindow {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidAuctionWindowException("End time must be after start time");
        }
    }

    public boolean isWithinWindow(Instant timestamp) {
        return !(timestamp.isBefore(startTime) || timestamp.isAfter(endTime));
    }
}
