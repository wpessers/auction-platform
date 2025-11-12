package wpessers.auctionservice.bid.infrastructure.out;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.bid.application.port.out.BidEventPublisher;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

@Component
public class SpringBidEventPublisherAdapter implements BidEventPublisher {

    @Override
    public void publishBidPlacedEvent(BidPlacedEvent event) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void publishBidRejectedEvent(BidRejectedEvent event) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
