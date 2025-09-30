package wpessers.auctionservice.domain;

import java.time.Instant;
import java.util.UUID;

public class Auction {

    private final UUID id;
    private final String name;
    private final String description;
    private final Instant endTime;

    public Auction(UUID id, String name, String description, Instant endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.endTime = endTime;
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

    public Instant getEndTime() {
        return endTime;
    }
}
