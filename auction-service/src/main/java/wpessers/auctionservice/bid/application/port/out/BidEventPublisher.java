package wpessers.auctionservice.bid.application.port.out;

import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

public interface BidEventPublisher {

    public void publishBidPlacedEvent(BidPlacedEvent event);

    public void publishBidRejectedEvent(BidRejectedEvent event);
}
