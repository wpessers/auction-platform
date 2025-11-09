package wpessers.auctionservice.auction.infrastructure.out.persistence.jpa;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.shared.domain.Money;

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

    public Auction toDomain(AuctionEntity entity) {
        return new Auction(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            new AuctionWindow(
                entity.getStartTime(),
                entity.getEndTime()
            ),
            new Money(entity.getStartingPrice()),
            entity.getStatus()
        );
    }
}
