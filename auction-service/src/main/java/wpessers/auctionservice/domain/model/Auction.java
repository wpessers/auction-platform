package wpessers.auctionservice.domain.model;

import wpessers.auctionservice.domain.exception.InvalidEndTimeException;
import java.time.Instant;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.isNull;

public class Auction {

    private static final long DEFAULT_AUCTION_DURATION_DAYS = 5L;

    private final UUID id;
    private final String name;
    private final String description;
    private final Instant endTime;
    private final Instant startTime;

    public Auction(UUID id, String name, String description, Instant startTime, Instant endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        if (isNull(endTime)) {
            this.endTime = startTime.plus(DEFAULT_AUCTION_DURATION_DAYS, DAYS);
        } else {
            if (endTime.isBefore(startTime)) {
                throw new InvalidEndTimeException("End time cannot be before start time");
            }
            this.endTime = endTime;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }
}
