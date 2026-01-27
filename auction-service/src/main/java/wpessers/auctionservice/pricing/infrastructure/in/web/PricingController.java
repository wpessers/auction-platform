package wpessers.auctionservice.pricing.infrastructure.in.web;

import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.pricing.application.port.out.PricingPort;

/**
 * REST controller for pricing suggestions and auction analysis.
 * Provides endpoints for the frontend to get AI-powered pricing insights.
 */
@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingPort pricingPort;
    private final AuctionStorage auctionRepository;

    public PricingController(PricingPort pricingPort, AuctionStorage auctionRepository) {
        this.pricingPort = pricingPort;
        this.auctionRepository = auctionRepository;
    }

    @GetMapping("/auctions/{auctionId}/suggestions")
    public ResponseEntity<BidSuggestionResponse> getBidSuggestions(@PathVariable UUID auctionId) {
        return auctionRepository.findById(auctionId)
                .map(this::getSuggestionsForAuction)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/auctions/{auctionId}/analysis")
    public ResponseEntity<AuctionAnalysisResponse> getAuctionAnalysis(@PathVariable UUID auctionId) {
        return auctionRepository.findById(auctionId)
                .map(this::getAnalysisForAuction)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private BidSuggestionResponse getSuggestionsForAuction(Auction auction) {
        long timeRemaining = Math.max(0,
                auction.getAuctionWindow().endTime().getEpochSecond() - Instant.now().getEpochSecond());

        double highestBid = auction.getHighestBid() != null
                ? auction.getHighestBid().amount().doubleValue()
                : 0.0;

        // Estimate bid count from version number (version increments with each bid)
        int estimatedBidCount = (int) auction.getBidVersion();

        PricingPort.BidSuggestion suggestion = pricingPort.getBidSuggestion(
                auction.getId(),
                highestBid,
                auction.getStartingPrice().amount().doubleValue(),
                timeRemaining,
                estimatedBidCount
        );

        return new BidSuggestionResponse(
                suggestion.conservativeBid(),
                suggestion.moderateBid(),
                suggestion.aggressiveBid(),
                suggestion.confidence(),
                suggestion.reasoning()
        );
    }

    private AuctionAnalysisResponse getAnalysisForAuction(Auction auction) {
        double highestBid = auction.getHighestBid() != null
                ? auction.getHighestBid().amount().doubleValue()
                : 0.0;

        // Estimate bid count from version number
        int estimatedBidCount = (int) auction.getBidVersion();

        PricingPort.AuctionAnalysis analysis = pricingPort.analyzeAuction(
                auction.getId(),
                auction.getName(),
                auction.getStartingPrice().amount().doubleValue(),
                highestBid,
                auction.getAuctionWindow().startTime().getEpochSecond(),
                auction.getAuctionWindow().endTime().getEpochSecond(),
                estimatedBidCount
        );

        return new AuctionAnalysisResponse(
                analysis.marketHeat().name(),
                analysis.estimatedFinalPrice(),
                analysis.priceTrend().name(),
                analysis.recommendation(),
                analysis.activitySummary()
        );
    }

    public record BidSuggestionResponse(
            double conservativeBid,
            double moderateBid,
            double aggressiveBid,
            double confidence,
            String reasoning
    ) {}

    public record AuctionAnalysisResponse(
            String marketHeat,
            double estimatedFinalPrice,
            String priceTrend,
            String recommendation,
            String activitySummary
    ) {}
}
