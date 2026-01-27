package wpessers.auctionservice.pricing.infrastructure.in.web;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.pricing.application.port.out.PricingPort;
import wpessers.auctionservice.shared.application.port.out.TokenParser;
import wpessers.auctionservice.shared.domain.Money;
import wpessers.auctionservice.shared.infrastructure.in.web.JwtAuthenticationFilter;
import wpessers.auctionservice.shared.infrastructure.in.web.SecurityConfig;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WebMvcTest(PricingController.class)
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PricingPort pricingPort;

    @MockitoBean
    private AuctionStorage auctionStorage;

    @MockitoBean
    private TokenParser tokenParser;

    @Test
    @WithMockUser
    @DisplayName("Should return bid suggestions for valid auction")
    void getBidSuggestions_returnsOkWithSuggestions() throws Exception {
        UUID auctionId = UUID.randomUUID();
        Auction auction = createTestAuction(auctionId);

        when(auctionStorage.findById(auctionId)).thenReturn(Optional.of(auction));
        when(pricingPort.getBidSuggestion(
                eq(auctionId), anyDouble(), anyDouble(), anyLong(), anyInt()
        )).thenReturn(new PricingPort.BidSuggestion(
                101.0, 105.0, 110.0, 0.85, "Test reasoning"
        ));

        mockMvc.perform(get("/api/pricing/auctions/{auctionId}/suggestions", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conservativeBid").value(101.0))
                .andExpect(jsonPath("$.moderateBid").value(105.0))
                .andExpect(jsonPath("$.aggressiveBid").value(110.0))
                .andExpect(jsonPath("$.confidence").value(0.85))
                .andExpect(jsonPath("$.reasoning").value("Test reasoning"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when auction not found for suggestions")
    void getBidSuggestions_returnsNotFoundForUnknownAuction() throws Exception {
        UUID auctionId = UUID.randomUUID();

        when(auctionStorage.findById(auctionId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pricing/auctions/{auctionId}/suggestions", auctionId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return auction analysis for valid auction")
    void getAuctionAnalysis_returnsOkWithAnalysis() throws Exception {
        UUID auctionId = UUID.randomUUID();
        Auction auction = createTestAuction(auctionId);

        when(auctionStorage.findById(auctionId)).thenReturn(Optional.of(auction));
        when(pricingPort.analyzeAuction(
                eq(auctionId), anyString(), anyDouble(), anyDouble(), anyLong(), anyLong(), anyInt()
        )).thenReturn(new PricingPort.AuctionAnalysis(
                PricingPort.MarketHeat.MEDIUM,
                125.0,
                PricingPort.PriceTrend.RISING,
                "Good time to bid",
                "5 bids in the last hour"
        ));

        mockMvc.perform(get("/api/pricing/auctions/{auctionId}/analysis", auctionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marketHeat").value("MEDIUM"))
                .andExpect(jsonPath("$.estimatedFinalPrice").value(125.0))
                .andExpect(jsonPath("$.priceTrend").value("RISING"))
                .andExpect(jsonPath("$.recommendation").value("Good time to bid"))
                .andExpect(jsonPath("$.activitySummary").value("5 bids in the last hour"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when auction not found for analysis")
    void getAuctionAnalysis_returnsNotFoundForUnknownAuction() throws Exception {
        UUID auctionId = UUID.randomUUID();

        when(auctionStorage.findById(auctionId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pricing/auctions/{auctionId}/analysis", auctionId))
                .andExpect(status().isNotFound());
    }

    private Auction createTestAuction(UUID id) {
        Instant now = Instant.now();
        AuctionWindow window = new AuctionWindow(now.minusSeconds(3600), now.plusSeconds(3600));
        return new Auction(
                id,
                "Test Auction",
                "Test Description",
                window,
                new Money(100.0),
                AuctionStatus.ACTIVE
        );
    }
}
