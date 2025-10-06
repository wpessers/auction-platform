package wpessers.auctionservice.fixtures;

import java.time.Instant;
import java.util.UUID;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;

public class AuctionBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "Default auction name";
    private String description = "Default description";
    private Instant startTime = Instant.parse("2025-01-01T00:00:00Z");
    private Instant endTime = Instant.parse("2025-01-02T00:00:00Z");
    private double startingPrice = 100.0;
    private AuctionStatus status = AuctionStatus.OPEN;

    public AuctionBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public AuctionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AuctionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public AuctionBuilder withTimeWindow(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public AuctionBuilder withStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
        return this;
    }

    public AuctionBuilder withStatus(AuctionStatus status) {
        this.status = status;
        return this;
    }

    public Auction build() {
        return new Auction(
            id,
            name,
            description,
            new AuctionWindow(startTime, endTime),
            new Money(startingPrice),
            status
        );
    }
}
