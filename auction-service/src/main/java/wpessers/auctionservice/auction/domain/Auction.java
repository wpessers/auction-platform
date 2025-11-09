package wpessers.auctionservice.auction.domain;

import java.time.Instant;
import java.util.UUID;
import wpessers.auctionservice.auction.domain.bidresult.AuctionNotActive;
import wpessers.auctionservice.auction.domain.bidresult.BidResult;
import wpessers.auctionservice.auction.domain.bidresult.Success;
import wpessers.auctionservice.auction.domain.exception.InvalidStartingPriceException;
import wpessers.auctionservice.auction.domain.exception.InvalidStateTransitionException;
import wpessers.auctionservice.shared.domain.Money;

public class Auction {

    private final UUID id;
    private final String name;
    private final String description;
    private final AuctionWindow auctionWindow;
    private final Money startingPrice;
    private AuctionStatus status;
    private Money highestBid;
    private UUID currentWinnerId;
    private long bidVersion;

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
        this.bidVersion = 0L;
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

    public Money getHighestBid() {
        return highestBid;
    }

    public UUID getCurrentWinnerId() {
        return currentWinnerId;
    }

    public long getBidVersion() {
        return bidVersion;
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

    public BidResult placeBid(UUID bidderId, Money bidAmount, Instant timestamp) {
        if (!isActive() || !auctionWindow.isWithinWindow(timestamp)) {
            return new AuctionNotActive();
        }

        UUID previousBidderId = this.currentWinnerId;
        Money previousAmount = this.highestBid;

        this.currentWinnerId = bidderId;
        this.highestBid = bidAmount;
        this.bidVersion++;
        return new Success(previousAmount, bidAmount, previousBidderId);
    }
}
