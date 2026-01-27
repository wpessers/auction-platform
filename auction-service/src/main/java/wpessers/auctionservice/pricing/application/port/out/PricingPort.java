package wpessers.auctionservice.pricing.application.port.out;

import java.util.UUID;

/**
 * Port for accessing pricing intelligence from the Price Service.
 * Provides bid suggestions and auction analysis.
 */
public interface PricingPort {

    /**
     * Get bid suggestions for an auction based on current state.
     *
     * @param auctionId The auction ID
     * @param currentHighestBid Current highest bid amount (0 if no bids)
     * @param startingPrice Starting price of the auction
     * @param timeRemainingSeconds Seconds until auction ends
     * @param totalBids Total number of bids placed
     * @return Bid suggestion with conservative, moderate, and aggressive options
     */
    BidSuggestion getBidSuggestion(
            UUID auctionId,
            double currentHighestBid,
            double startingPrice,
            long timeRemainingSeconds,
            int totalBids
    );

    /**
     * Analyze an auction's pricing and market activity.
     *
     * @param auctionId The auction ID
     * @param auctionName Name of the auction
     * @param startingPrice Starting price
     * @param currentHighestBid Current highest bid (0 if no bids)
     * @param startTimeEpoch Auction start time as epoch seconds
     * @param endTimeEpoch Auction end time as epoch seconds
     * @param totalBids Total number of bids
     * @return Analysis with market heat, price trend, and recommendations
     */
    AuctionAnalysis analyzeAuction(
            UUID auctionId,
            String auctionName,
            double startingPrice,
            double currentHighestBid,
            long startTimeEpoch,
            long endTimeEpoch,
            int totalBids
    );

    /**
     * Bid suggestion with three strategy levels.
     */
    record BidSuggestion(
            double conservativeBid,
            double moderateBid,
            double aggressiveBid,
            double confidence,
            String reasoning
    ) {}

    /**
     * Auction analysis with market insights.
     */
    record AuctionAnalysis(
            MarketHeat marketHeat,
            double estimatedFinalPrice,
            PriceTrend priceTrend,
            String recommendation,
            String activitySummary
    ) {}

    enum MarketHeat {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }

    enum PriceTrend {
        STABLE, RISING, RISING_FAST
    }
}
