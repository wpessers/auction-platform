package wpessers.auctionservice.domain.model;

import wpessers.auctionservice.domain.exception.InvalidEndTimeException;
import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;
import java.time.Instant;
import java.util.UUID;

public class Auction {

    private static final long DEFAULT_AUCTION_DURATION_DAYS = 5L;

    private final UUID id;
    private final String name;
    private final String description;
    // TODO: Refactor into an auction window value object?
    private final Instant endTime;
    private final Instant startTime;
    private final Money startingPrice;

    private Auction(UUID id, String name, String description, Instant startTime, Instant endTime,
        Money startingPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startingPrice = startingPrice;
    }

    public static Auction create(
        UUID id,
        String name,
        String description,
        Instant startTime,
        Instant endTime,
        Money startingPrice
    ) {
        if (endTime.isBefore(startTime)) {
            throw new InvalidEndTimeException("End time cannot be before start time");
        }
        if (startingPrice.isNegative()) {
            throw new InvalidStartingPriceException("Starting price cannot be negative");
        }
        return new Auction(id, name, description, startTime, endTime, startingPrice);
    }
}
