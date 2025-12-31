package wpessers.auctionservice.bid.infrastructure.in.web;

import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import wpessers.auctionservice.bid.application.BidService;
import wpessers.auctionservice.bid.application.port.in.PlaceBidCommand;
import wpessers.auctionservice.shared.application.port.out.UserClaims;

@ExtendWith(MockitoExtension.class)
class BidControllerTest {

    @Mock
    private BidService bidService;

    private BidController bidController;

    @BeforeEach
    void setUp() {
        bidController = new BidController(bidService);
    }

    @Test
    @DisplayName("Should place a bid for the authenticated user")
    void shouldPlaceBid() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID auctionId = UUID.randomUUID();
        BigDecimal bidAmount = new BigDecimal(150);

        UserClaims claims = new UserClaims(userId, "user");
        Principal principal = new UsernamePasswordAuthenticationToken(claims, null);
        PlaceBidMessage message = new PlaceBidMessage(auctionId, bidAmount);

        // When
        bidController.placeBid(message, principal);

        // Then
        verify(bidService).placeBid(new PlaceBidCommand(userId, auctionId, bidAmount));
    }
}