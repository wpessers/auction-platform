package wpessers.auctionservice.fixtures;

import java.time.Instant;
import java.util.UUID;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;

public class AuctionTestDataBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "Default auction name";
    private String description = "Default description";
    private Instant startTime = Instant.parse("2025-01-01T00:00:00Z");
    private Instant endTime = Instant.parse("2025-01-02T00:00:00Z");
    private double price = 100.0;
    private AuctionStatus status = AuctionStatus.ACTIVE;

    public AuctionTestDataBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public AuctionTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AuctionTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public AuctionTestDataBuilder withTimeWindow(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public AuctionTestDataBuilder withPrice(double amount) {
        this.price = amount;
        return this;
    }

    public AuctionTestDataBuilder withStatus(AuctionStatus status) {
        this.status = status;
        return this;
    }

    public Auction build() {
        Auction auction = Auction.create(
            id,
            name,
            description,
            new AuctionWindow(startTime, endTime),
            new Money(price)
        );
        auction.setStatus(status);
        return auction;
    }
}
