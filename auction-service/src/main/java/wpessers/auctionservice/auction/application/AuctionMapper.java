package wpessers.auctionservice.auction.application;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.in.AuctionResponse;
import wpessers.auctionservice.auction.domain.Auction;

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
