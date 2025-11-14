package wpessers.auctionservice.bid.infrastructure.out;

import java.util.ArrayList;
import java.util.List;
import wpessers.auctionservice.bid.application.port.out.BidEventPublisher;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

public class FakeBidEventPublisherAdapter implements BidEventPublisher {

    private final List<BidPlacedEvent> placedEvents;
    private final List<BidRejectedEvent> rejectedEvents;

    public FakeBidEventPublisherAdapter() {
        this.placedEvents = new ArrayList<>();
        this.rejectedEvents = new ArrayList<>();
    }

    @Override
    public void publishBidPlacedEvent(BidPlacedEvent event) {
        placedEvents.add(event);
    }

    @Override
    public void publishBidRejectedEvent(BidRejectedEvent event) {
        rejectedEvents.add(event);
    }

    public List<BidPlacedEvent> getPlacedEvents() {
        return placedEvents;
    }

    public List<BidRejectedEvent> getRejectedEvents() {
        return rejectedEvents;
    }
}
