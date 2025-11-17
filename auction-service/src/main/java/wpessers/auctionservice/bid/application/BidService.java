package wpessers.auctionservice.bid.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.bidresult.AuctionNotActive;
import wpessers.auctionservice.auction.domain.bidresult.BidAmountTooLow;
import wpessers.auctionservice.auction.domain.bidresult.BidResult;
import wpessers.auctionservice.auction.domain.bidresult.Success;
import wpessers.auctionservice.bid.application.port.in.PlaceBidCommand;
import wpessers.auctionservice.bid.application.port.out.BidEventPublisher;
import wpessers.auctionservice.bid.application.port.out.BidStorage;
import wpessers.auctionservice.bid.domain.Bid;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;
import wpessers.auctionservice.bid.domain.event.RejectionReason;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;
import wpessers.auctionservice.shared.domain.Money;

@Service
public class BidService {

    private final AuctionRegistry auctionRegistry;
    private final BidStorage bidStorage;
    private final BidEventPublisher bidEventPublisher;
    private final TimeProvider timeProvider;

    public BidService(AuctionRegistry auctionRegistry,
        BidStorage bidStorage,
        BidEventPublisher bidEventPublisher,
        TimeProvider timeProvider) {
        this.auctionRegistry = auctionRegistry;
        this.bidStorage = bidStorage;
        this.bidEventPublisher = bidEventPublisher;
        this.timeProvider = timeProvider;
    }


    public void placeBid(PlaceBidCommand command) {
        Money bidAmount = new Money(command.amount());
        BidResult result = auctionRegistry.executeOnAuction(
            command.auctionId(),
            auction -> auction.placeBid(command.bidderId(), bidAmount, timeProvider.now())
        );

        switch (result) {
            case AuctionNotActive ignored -> bidEventPublisher.publishBidRejectedEvent(
                new BidRejectedEvent(
                    command.auctionId(),
                    command.bidderId(),
                    bidAmount,
                    RejectionReason.AUCTION_CLOSED
                )
            );
            case BidAmountTooLow ignored -> bidEventPublisher.publishBidRejectedEvent(
                new BidRejectedEvent(
                    command.auctionId(),
                    command.bidderId(),
                    bidAmount,
                    RejectionReason.BID_TOO_LOW
                )
            );
            case Success success -> {
                bidStorage.save(
                    new Bid(command.auctionId(), command.bidderId(), bidAmount, timeProvider.now())
                );
                bidEventPublisher.publishBidPlacedEvent(
                    new BidPlacedEvent(
                        command.auctionId(),
                        command.bidderId(),
                        success.previousBidder(),
                        bidAmount
                    )
                );
            }
        }
    }
}
