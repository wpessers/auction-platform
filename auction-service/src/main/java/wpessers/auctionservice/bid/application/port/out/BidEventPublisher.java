package wpessers.auctionservice.bid.application.port.out;

import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

public interface BidEventPublisher {

    void publishBidPlacedEvent(BidPlacedEvent event);

    void publishBidRejectedEvent(BidRejectedEvent event);
}
