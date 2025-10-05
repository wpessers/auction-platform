package wpessers.auctionservice.application;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.in.query.AuctionResponse;
import wpessers.auctionservice.domain.Auction;

@Component
public class AuctionMapper {

    public AuctionResponse toResponse(Auction auction) {
        return new AuctionResponse(
            auction.getId(),
            auction.getName(),
            auction.getDescription(),
            auction.getAuctionWindow().startTime(),
            auction.getAuctionWindow().endTime(),
            auction.getStartingPrice().amount()
        );
    }
}
