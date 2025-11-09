package wpessers.auctionservice.fixtures;

import java.math.BigDecimal;
import java.time.Instant;
import wpessers.auctionservice.auction.application.port.in.CreateAuctionCommand;

public class CreateAuctionCommandBuilder {

    private String name = "Default auction name";
    private String description = "Default description";
    private Instant startTime = Instant.parse("2025-01-01T00:00:00Z");
    private Instant endTime = Instant.parse("2025-01-02T00:00:00Z");
    private BigDecimal startingPrice = BigDecimal.valueOf(100);

    public CreateAuctionCommandBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CreateAuctionCommandBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public CreateAuctionCommandBuilder withStartTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public CreateAuctionCommandBuilder withEndTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public CreateAuctionCommandBuilder withStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
        return this;
    }

    public CreateAuctionCommand build() {
        return new CreateAuctionCommand(
            name,
            description,
            startTime,
            endTime,
            startingPrice
        );
    }
}
