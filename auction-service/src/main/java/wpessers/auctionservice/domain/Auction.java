package wpessers.auctionservice.domain;

import java.util.UUID;
import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;
import wpessers.auctionservice.domain.exception.InvalidStateTransitionException;

public class Auction {

    private final UUID id;
    private final String name;
    private final String description;
    private final AuctionWindow auctionWindow;
    private final Money startingPrice;
    private AuctionStatus status;

    public Auction(
        UUID id,
        String name,
        String description,
        AuctionWindow auctionWindow,
        Money startingPrice,
        AuctionStatus status
    ) {
        if (startingPrice.isNegative()) {
            throw new InvalidStartingPriceException("Starting price cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.auctionWindow = auctionWindow;
        this.startingPrice = startingPrice;
        this.status = status;
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

    public boolean isActive() {
        return AuctionStatus.ACTIVE.equals(status);
    }

    public boolean isClosed() {
        return AuctionStatus.CLOSED.equals(status);
    }

    public void start() {
        if (isActive()) {
            throw new InvalidStateTransitionException("Auction is already open");
        }
        if (isClosed()) {
            throw new InvalidStateTransitionException("Cannot start a closed auction");
        }
        status = AuctionStatus.ACTIVE;
    }

    public void end() {
        if (isClosed()) {
            throw new InvalidStateTransitionException("Auction is already closed");
        }
        status = AuctionStatus.CLOSED;
    }
}
