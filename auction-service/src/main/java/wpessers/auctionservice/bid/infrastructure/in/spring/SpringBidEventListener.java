package wpessers.auctionservice.bid.infrastructure.in.spring;

import java.math.BigDecimal;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

@Component
public class SpringBidEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public SpringBidEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleBidPlacedEvent(BidPlacedEvent event) {
        String broadcastDestination = "/topic/auctions/" + event.auctionId();
        BigDecimal bidAmount = event.amount().amount();
        BidPlacedMessage placedMessage = new BidPlacedMessage(event.bidderId(), bidAmount);
        messagingTemplate.convertAndSend(broadcastDestination, placedMessage);

        if (event.previousBidderId() != null) {
            String userId = event.previousBidderId().toString();
            String userDestination = "/queue/notifications";
            OutbidMessage outbidMessage = new OutbidMessage(event.auctionId(), bidAmount);
            messagingTemplate.convertAndSendToUser(userId, userDestination, outbidMessage);
        }
    }

    @EventListener
    public void handleBidRejectedEvent(BidRejectedEvent event) {
        String userId = event.bidderId().toString();
        String destination = "/queue/errors";
        BidRejectedMessage message = new BidRejectedMessage(
            event.auctionId(),
            event.reason().toString()
        );
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }
}
