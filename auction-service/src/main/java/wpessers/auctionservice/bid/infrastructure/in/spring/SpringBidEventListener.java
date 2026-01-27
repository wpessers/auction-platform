package wpessers.auctionservice.bid.infrastructure.in.spring;

import java.math.BigDecimal;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;

@Component
public class SpringBidEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final AuctionStorage auctionStorage;

    public SpringBidEventListener(SimpMessagingTemplate messagingTemplate, AuctionStorage auctionStorage) {
        this.messagingTemplate = messagingTemplate;
        this.auctionStorage = auctionStorage;
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
            String auctionName = auctionStorage.findById(event.auctionId())
                .map(auction -> auction.getName())
                .orElse("Unknown Auction");
            OutbidMessage outbidMessage = new OutbidMessage(event.auctionId(), auctionName, bidAmount);
            messagingTemplate.convertAndSendToUser(userId, userDestination, outbidMessage);
        }
    }

    @EventListener
    public void handleBidRejectedEvent(BidRejectedEvent event) {
        String userId = event.bidderId().toString();
        String destination = "/queue/errors";
        BidRejectedMessage message = new BidRejectedMessage(
            event.bidderId(),
            event.reason().toString()
        );
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }
}
