package wpessers.auctionservice.domain;

import static java.util.UUID.randomUUID;

import java.time.Instant;
import java.util.UUID;

public class Auction {

    private final UUID id;
    private final String name;
    private final String description;
    private final Instant endTime;

    public Auction(String name, String description, Instant endTime) {
        this.id = randomUUID();
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
