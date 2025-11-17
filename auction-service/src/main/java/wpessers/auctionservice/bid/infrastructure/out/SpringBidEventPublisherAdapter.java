package wpessers.auctionservice.bid.infrastructure.out;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.bid.application.port.out.BidEventPublisher;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

@Component
public class SpringBidEventPublisherAdapter implements BidEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringBidEventPublisherAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publishBidPlacedEvent(BidPlacedEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishBidRejectedEvent(BidRejectedEvent event) {
        publisher.publishEvent(event);
    }
}
