package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.domain.Auction;

@Component
public class AuctionEntityMapper {

    public AuctionEntity toEntity(Auction auction) {
        AuctionEntity entity = new AuctionEntity();
        entity.setId(auction.getId());
        entity.setName(auction.getName());
        entity.setDescription(auction.getDescription());
        entity.setStartTime(auction.getAuctionWindow().startTime());
        entity.setEndTime(auction.getAuctionWindow().endTime());
        entity.setStartingPrice(auction.getStartingPrice().amount());
        entity.setStatus(auction.getStatus());
        return entity;
    }
}
