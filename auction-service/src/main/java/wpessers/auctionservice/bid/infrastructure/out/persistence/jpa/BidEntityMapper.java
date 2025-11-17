package wpessers.auctionservice.bid.infrastructure.out.persistence.jpa;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.bid.domain.Bid;

@Component
public class BidEntityMapper {

    public BidEntity toEntity(Bid bid) {
        BidEntity entity = new BidEntity();
        entity.setAuctionId(bid.auctionId());
        entity.setBidderId(bid.bidderId());
        entity.setAmount(bid.amount().amount());
        entity.setTimestamp(bid.timestamp());
        return entity;
    }
}
