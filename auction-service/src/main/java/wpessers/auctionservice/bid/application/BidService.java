package wpessers.auctionservice.bid.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.bidresult.AuctionNotActive;
import wpessers.auctionservice.auction.domain.bidresult.BidAmountTooLow;
import wpessers.auctionservice.auction.domain.bidresult.BidResult;
import wpessers.auctionservice.auction.domain.bidresult.Success;
import wpessers.auctionservice.bid.application.port.in.PlaceBidCommand;
import wpessers.auctionservice.bid.application.port.out.BidEventPublisher;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;
import wpessers.auctionservice.shared.domain.Money;
import java.util.Map;

@Service
public class BidService {

    private final AuctionRegistry auctionRegistry;
    private final BidEventPublisher bidEventPublisher;
    private final TimeProvider timeProvider;
//    Map

    public BidService(AuctionRegistry auctionRegistry, BidEventPublisher bidEventPublisher,
        TimeProvider timeProvider) {
        this.auctionRegistry = auctionRegistry;
        this.bidEventPublisher = bidEventPublisher;
        this.timeProvider = timeProvider;
    }


    public void placeBid(PlaceBidCommand placeBidCommand) {
        BidResult result = auctionRegistry.executeOnAuction(placeBidCommand.auctionId(),
            auction -> auction.placeBid(
                placeBidCommand.bidderId(),
                new Money(placeBidCommand.amount()),
                timeProvider.now()));

        switch (result) {
            case AuctionNotActive auctionNotActive -> {
            }
            case BidAmountTooLow bidAmountTooLow -> {
            }
            case Success success -> {
            }
        }
    }
}
