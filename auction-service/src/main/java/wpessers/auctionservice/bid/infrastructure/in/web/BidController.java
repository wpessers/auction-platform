package wpessers.auctionservice.bid.infrastructure.in.web;

import java.security.Principal;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
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
}
