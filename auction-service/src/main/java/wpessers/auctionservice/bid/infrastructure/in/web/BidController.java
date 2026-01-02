package wpessers.auctionservice.bid.infrastructure.in.web;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import wpessers.auctionservice.auction.domain.exception.AuctionNotFoundException;
import wpessers.auctionservice.bid.application.BidService;
import wpessers.auctionservice.bid.application.port.in.PlaceBidCommand;
import wpessers.auctionservice.shared.application.port.out.UserClaims;

@Controller
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @MessageMapping("/bid")
    public void placeBid(@Payload @Valid PlaceBidMessage message, Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken authToken) {
            UserClaims claims = (UserClaims) authToken.getPrincipal();
            bidService.placeBid(new PlaceBidCommand(
                claims.userId(),
                message.auctionId(),
                message.amount()
            ));
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleAuctionNotFoundException(AuctionNotFoundException ex) {
        return ex.getMessage();
    }
}
