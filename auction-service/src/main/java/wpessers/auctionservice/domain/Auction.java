package wpessers.auctionservice.domain;

import java.util.UUID;
import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;

public class Auction {

    private final UUID id;
    private final String name;
    private final String description;
    private final AuctionWindow auctionWindow;
    private final Money startingPrice;
    private AuctionStatus status;

    private Auction(
        UUID id,
        String name,
        String description,
        AuctionWindow auctionWindow,
        Money startingPrice,
        AuctionStatus status
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.auctionWindow = auctionWindow;
        this.startingPrice = startingPrice;
        this.status = status;
    }

    public static Auction create(
        UUID id,
        String name,
        String description,
        AuctionWindow auctionWindow,
        Money startingPrice
    ) {
        if (startingPrice.isNegative()) {
            throw new InvalidStartingPriceException("Starting price cannot be negative");
        }

        return new Auction(
            id,
            name,
            description,
            auctionWindow,
            startingPrice,
            AuctionStatus.ACTIVE
        );
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

    public AuctionWindow getAuctionWindow() {
        return auctionWindow;
    }

    public Money getStartingPrice() {
        return startingPrice;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }
}
