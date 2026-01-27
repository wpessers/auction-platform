package wpessers.auctionservice.auction.infrastructure.out.cache.distributed;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.shared.domain.Money;

/**
 * Data Transfer Object for serializing Auction to Redis.
 * This DTO flattens the domain model for simpler JSON serialization.
 */
public class AuctionCacheDto {

    private String id;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private String startingPrice;
    private String status;
    private String highestBid;
    private String currentWinnerId;
    private long bidVersion;

    public AuctionCacheDto() {
    }

    public static AuctionCacheDto fromAuction(Auction auction) {
        AuctionCacheDto dto = new AuctionCacheDto();
        dto.id = auction.getId().toString();
        dto.name = auction.getName();
        dto.description = auction.getDescription();
        dto.startTime = auction.getAuctionWindow().startTime().toString();
        dto.endTime = auction.getAuctionWindow().endTime().toString();
        dto.startingPrice = auction.getStartingPrice().amount().toPlainString();
        dto.status = auction.getStatus().name();
        dto.highestBid = auction.getHighestBid() != null
            ? auction.getHighestBid().amount().toPlainString()
            : null;
        dto.currentWinnerId = auction.getCurrentWinnerId() != null
            ? auction.getCurrentWinnerId().toString()
            : null;
        dto.bidVersion = auction.getBidVersion();
        return dto;
    }

    public Auction toAuction() {
        return Auction.reconstruct(
            UUID.fromString(id),
            name,
            description,
            new AuctionWindow(Instant.parse(startTime), Instant.parse(endTime)),
            new Money(new BigDecimal(startingPrice)),
            AuctionStatus.valueOf(status),
            highestBid != null ? new Money(new BigDecimal(highestBid)) : null,
            currentWinnerId != null ? UUID.fromString(currentWinnerId) : null,
            bidVersion
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(String startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(String highestBid) {
        this.highestBid = highestBid;
    }

    public String getCurrentWinnerId() {
        return currentWinnerId;
    }

    public void setCurrentWinnerId(String currentWinnerId) {
        this.currentWinnerId = currentWinnerId;
    }

    public long getBidVersion() {
        return bidVersion;
    }

    public void setBidVersion(long bidVersion) {
        this.bidVersion = bidVersion;
    }
}
