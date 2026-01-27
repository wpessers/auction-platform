package wpessers.auctionservice.pricing.infrastructure.out.grpc;

import io.grpc.StatusRuntimeException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.pricing.application.port.out.PricingPort;
import wpessers.priceservice.grpc.AuctionAnalysisRequest;
import wpessers.priceservice.grpc.AuctionAnalysisResponse;
import wpessers.priceservice.grpc.BidSuggestionRequest;
import wpessers.priceservice.grpc.BidSuggestionResponse;
import wpessers.priceservice.grpc.PricingServiceGrpc;

/**
 * gRPC adapter for communicating with the Price Service.
 * Provides bid suggestions and auction analysis via gRPC calls.
 */
@Component
public class PricingGrpcAdapter implements PricingPort {

    private static final Logger log = LoggerFactory.getLogger(PricingGrpcAdapter.class);

    private final PricingServiceGrpc.PricingServiceBlockingStub pricingStub;

    public PricingGrpcAdapter(GrpcChannelFactory channelFactory, PricingServiceConfig config) {
        var channel = channelFactory.createChannel(config.address());
        this.pricingStub = PricingServiceGrpc.newBlockingStub(channel);
        log.info("Initialized Pricing gRPC adapter connecting to {}", config.address());
    }

    @Override
    public BidSuggestion getBidSuggestion(
            UUID auctionId,
            double currentHighestBid,
            double startingPrice,
            long timeRemainingSeconds,
            int totalBids
    ) {
        log.debug("Requesting bid suggestion for auction {}", auctionId);

        try {
            BidSuggestionRequest request = BidSuggestionRequest.newBuilder()
                    .setAuctionId(auctionId.toString())
                    .setCurrentHighestBid(currentHighestBid)
                    .setStartingPrice(startingPrice)
                    .setTimeRemainingSeconds(timeRemainingSeconds)
                    .setTotalBids(totalBids)
                    .build();

            BidSuggestionResponse response = pricingStub.getBidSuggestion(request);

            log.debug("Received bid suggestion for auction {}: conservative={}, moderate={}, aggressive={}",
                    auctionId, response.getConservativeBid(), response.getModerateBid(), response.getAggressiveBid());

            return new BidSuggestion(
                    response.getConservativeBid(),
                    response.getModerateBid(),
                    response.getAggressiveBid(),
                    response.getConfidence(),
                    response.getReasoning()
            );
        } catch (StatusRuntimeException e) {
            log.error("gRPC error getting bid suggestion for auction {}: {}", auctionId, e.getStatus());
            return getFallbackBidSuggestion(currentHighestBid, startingPrice);
        }
    }

    @Override
    public AuctionAnalysis analyzeAuction(
            UUID auctionId,
            String auctionName,
            double startingPrice,
            double currentHighestBid,
            long startTimeEpoch,
            long endTimeEpoch,
            int totalBids
    ) {
        log.debug("Requesting auction analysis for auction {}", auctionId);

        try {
            AuctionAnalysisRequest request = AuctionAnalysisRequest.newBuilder()
                    .setAuctionId(auctionId.toString())
                    .setAuctionName(auctionName)
                    .setStartingPrice(startingPrice)
                    .setCurrentHighestBid(currentHighestBid)
                    .setStartTimeEpoch(startTimeEpoch)
                    .setEndTimeEpoch(endTimeEpoch)
                    .setTotalBids(totalBids)
                    .build();

            AuctionAnalysisResponse response = pricingStub.analyzeAuction(request);

            log.debug("Received auction analysis for {}: heat={}, trend={}, estimatedFinal={}",
                    auctionId, response.getMarketHeat(), response.getPriceTrend(), response.getEstimatedFinalPrice());

            return new AuctionAnalysis(
                    mapMarketHeat(response.getMarketHeat()),
                    response.getEstimatedFinalPrice(),
                    mapPriceTrend(response.getPriceTrend()),
                    response.getRecommendation(),
                    response.getActivitySummary()
            );
        } catch (StatusRuntimeException e) {
            log.error("gRPC error analyzing auction {}: {}", auctionId, e.getStatus());
            return getFallbackAnalysis(currentHighestBid, startingPrice);
        }
    }

    private MarketHeat mapMarketHeat(wpessers.priceservice.grpc.MarketHeat grpcHeat) {
        return switch (grpcHeat) {
            case LOW -> MarketHeat.LOW;
            case MEDIUM -> MarketHeat.MEDIUM;
            case HIGH -> MarketHeat.HIGH;
            case VERY_HIGH -> MarketHeat.VERY_HIGH;
            default -> MarketHeat.LOW;
        };
    }

    private PriceTrend mapPriceTrend(wpessers.priceservice.grpc.PriceTrend grpcTrend) {
        return switch (grpcTrend) {
            case STABLE -> PriceTrend.STABLE;
            case RISING -> PriceTrend.RISING;
            case RISING_FAST -> PriceTrend.RISING_FAST;
            default -> PriceTrend.STABLE;
        };
    }

    private BidSuggestion getFallbackBidSuggestion(double currentHighestBid, double startingPrice) {
        double basePrice = currentHighestBid > 0 ? currentHighestBid : startingPrice;
        double increment = Math.max(basePrice * 0.01, 1.0);
        return new BidSuggestion(
                Math.round((basePrice + increment) * 100.0) / 100.0,
                Math.round((basePrice + increment * 3) * 100.0) / 100.0,
                Math.round((basePrice + increment * 5) * 100.0) / 100.0,
                0.0,
                "Price service unavailable. Basic suggestions provided."
        );
    }

    private AuctionAnalysis getFallbackAnalysis(double currentHighestBid, double startingPrice) {
        return new AuctionAnalysis(
                MarketHeat.LOW,
                currentHighestBid > 0 ? currentHighestBid : startingPrice,
                PriceTrend.STABLE,
                "Price service unavailable. Analysis not available.",
                "Unable to analyze auction activity."
        );
    }
}
