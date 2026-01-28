package wpessers.priceservice.infrastructure.grpc

import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService
import wpessers.priceservice.grpc.*
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * gRPC implementation of the Pricing Service.
 *
 * Provides intelligent pricing suggestions based on auction state,
 * time remaining, and bidding activity. Uses algorithmic analysis
 * to calculate bid recommendations and market insights.
 */
@GrpcService
class PricingServiceImpl : PricingServiceGrpc.PricingServiceImplBase() {

    private val logger = LoggerFactory.getLogger(PricingServiceImpl::class.java)

    override fun getBidSuggestion(
        request: BidSuggestionRequest,
        responseObserver: StreamObserver<BidSuggestionResponse>
    ) {
        logger.info("Received bid suggestion request for auction: {}", request.auctionId)

        try {
            val response = calculateBidSuggestion(request)
            responseObserver.onNext(response)
            responseObserver.onCompleted()
            logger.info("Bid suggestion sent for auction {}: conservative={}, moderate={}, aggressive={}",
                request.auctionId, response.conservativeBid, response.moderateBid, response.aggressiveBid)
        } catch (e: Exception) {
            logger.error("Error calculating bid suggestion for auction {}: {}", request.auctionId, e.message)
            responseObserver.onError(
                io.grpc.Status.INTERNAL
                    .withDescription("Error calculating bid suggestion: ${e.message}")
                    .asRuntimeException()
            )
        }
    }

    override fun analyzeAuction(
        request: AuctionAnalysisRequest,
        responseObserver: StreamObserver<AuctionAnalysisResponse>
    ) {
        logger.info("Received auction analysis request for auction: {}", request.auctionId)

        try {
            val response = analyzeAuctionPricing(request)
            responseObserver.onNext(response)
            responseObserver.onCompleted()
            logger.info("Auction analysis sent for {}: heat={}, trend={}, estimatedFinal={}",
                request.auctionId, response.marketHeat, response.priceTrend, response.estimatedFinalPrice)
        } catch (e: Exception) {
            logger.error("Error analyzing auction {}: {}", request.auctionId, e.message)
            responseObserver.onError(
                io.grpc.Status.INTERNAL
                    .withDescription("Error analyzing auction: ${e.message}")
                    .asRuntimeException()
            )
        }
    }

    private fun calculateBidSuggestion(request: BidSuggestionRequest): BidSuggestionResponse {
        val currentPrice = if (request.currentHighestBid > 0) request.currentHighestBid else request.startingPrice
        val timeRemaining = request.timeRemainingSeconds
        val totalBids = request.totalBids

        // Calculate urgency factor based on time remaining
        // Higher urgency = larger bid increments suggested
        val urgencyFactor = calculateUrgencyFactor(timeRemaining)

        // Calculate competition factor based on bid count
        val competitionFactor = calculateCompetitionFactor(totalBids)

        // Base increment is 1% of current price, minimum $1
        val baseIncrement = max(currentPrice * 0.01, 1.0)

        // Calculate bid suggestions
        val conservativeIncrement = baseIncrement * (1.0 + urgencyFactor * 0.5)
        val moderateIncrement = baseIncrement * (2.0 + urgencyFactor + competitionFactor)
        val aggressiveIncrement = baseIncrement * (5.0 + urgencyFactor * 2 + competitionFactor * 2)

        val conservativeBid = roundToTwoDecimals(currentPrice + conservativeIncrement)
        val moderateBid = roundToTwoDecimals(currentPrice + moderateIncrement)
        val aggressiveBid = roundToTwoDecimals(currentPrice + aggressiveIncrement)

        // Calculate confidence based on data quality
        val confidence = calculateConfidence(totalBids, timeRemaining)

        // Generate reasoning
        val reasoning = generateBidReasoning(timeRemaining, totalBids)

        return BidSuggestionResponse.newBuilder()
            .setConservativeBid(conservativeBid)
            .setModerateBid(moderateBid)
            .setAggressiveBid(aggressiveBid)
            .setConfidence(confidence)
            .setReasoning(reasoning)
            .build()
    }

    private fun analyzeAuctionPricing(request: AuctionAnalysisRequest): AuctionAnalysisResponse {
        val currentPrice = if (request.currentHighestBid > 0) request.currentHighestBid else request.startingPrice
        val startingPrice = request.startingPrice
        val totalBids = request.totalBids
        val auctionDuration = request.endTimeEpoch - request.startTimeEpoch
        val currentTime = System.currentTimeMillis() / 1000
        val elapsed = currentTime - request.startTimeEpoch
        val remaining = request.endTimeEpoch - currentTime

        // Calculate market heat
        val marketHeat = calculateMarketHeat(totalBids, auctionDuration, elapsed)

        // Calculate price trend
        val priceTrend = calculatePriceTrend(currentPrice, startingPrice, totalBids, elapsed, auctionDuration)

        // Estimate final price
        val estimatedFinalPrice = estimateFinalPrice(currentPrice, startingPrice, totalBids, remaining, auctionDuration)

        // Generate recommendation
        val recommendation = generateRecommendation(marketHeat, priceTrend, remaining, totalBids)

        // Activity summary
        val activitySummary = generateActivitySummary(totalBids, elapsed, currentPrice, startingPrice)

        return AuctionAnalysisResponse.newBuilder()
            .setMarketHeat(marketHeat)
            .setEstimatedFinalPrice(roundToTwoDecimals(estimatedFinalPrice))
            .setPriceTrend(priceTrend)
            .setRecommendation(recommendation)
            .setActivitySummary(activitySummary)
            .build()
    }

    private fun calculateUrgencyFactor(timeRemainingSeconds: Long): Double {
        return when {
            timeRemainingSeconds <= 60 -> 3.0      // Last minute - very urgent
            timeRemainingSeconds <= 300 -> 2.0    // Last 5 minutes - urgent
            timeRemainingSeconds <= 900 -> 1.5    // Last 15 minutes - moderate urgency
            timeRemainingSeconds <= 3600 -> 1.0   // Last hour - slight urgency
            timeRemainingSeconds <= 86400 -> 0.5  // Last day - low urgency
            else -> 0.0                            // More than a day - no urgency
        }
    }

    private fun calculateCompetitionFactor(totalBids: Int): Double {
        return when {
            totalBids == 0 -> 0.0
            totalBids <= 3 -> 0.5
            totalBids <= 10 -> 1.0
            totalBids <= 25 -> 1.5
            totalBids <= 50 -> 2.0
            else -> 2.5
        }
    }

    private fun calculateConfidence(totalBids: Int, timeRemaining: Long): Double {
        // More bids and less time = higher confidence in suggestions
        val bidConfidence = min(totalBids / 10.0, 1.0)
        val timeConfidence = when {
            timeRemaining <= 300 -> 0.9
            timeRemaining <= 3600 -> 0.7
            timeRemaining <= 86400 -> 0.5
            else -> 0.3
        }
        return roundToTwoDecimals((bidConfidence + timeConfidence) / 2)
    }

    private fun calculateMarketHeat(totalBids: Int, auctionDurationSec: Long, elapsedSec: Long): MarketHeat {
        if (elapsedSec <= 0 || auctionDurationSec <= 0) return MarketHeat.LOW

        val elapsedHours = max(elapsedSec / 3600.0, 0.1)
        val bidsPerHour = totalBids / elapsedHours

        return when {
            bidsPerHour >= 10 -> MarketHeat.VERY_HIGH
            bidsPerHour >= 5 -> MarketHeat.HIGH
            bidsPerHour >= 2 -> MarketHeat.MEDIUM
            else -> MarketHeat.LOW
        }
    }

    private fun calculatePriceTrend(
        currentPrice: Double,
        startingPrice: Double,
        totalBids: Int,
        elapsedSec: Long,
        auctionDurationSec: Long
    ): PriceTrend {
        if (totalBids == 0 || startingPrice <= 0) return PriceTrend.STABLE

        val priceIncrease = (currentPrice - startingPrice) / startingPrice
        val progressPercent = if (auctionDurationSec > 0) elapsedSec.toDouble() / auctionDurationSec else 0.0

        // Normalize price increase by time progress
        val normalizedIncrease = if (progressPercent > 0) priceIncrease / progressPercent else priceIncrease

        return when {
            normalizedIncrease >= 0.5 -> PriceTrend.RISING_FAST
            normalizedIncrease >= 0.1 -> PriceTrend.RISING
            else -> PriceTrend.STABLE
        }
    }

    private fun estimateFinalPrice(
        currentPrice: Double,
        startingPrice: Double,
        totalBids: Int,
        remainingSec: Long,
        auctionDurationSec: Long
    ): Double {
        if (totalBids == 0) return currentPrice

        // Calculate historical price growth rate from starting price
        val priceGrowthRate = if (startingPrice > 0) {
            (currentPrice - startingPrice) / startingPrice
        } else {
            0.0
        }

        // Use logarithmic growth model for price estimation
        val remainingPercent = if (auctionDurationSec > 0) remainingSec.toDouble() / auctionDurationSec else 0.0

        // Factor in historical momentum - auctions with higher growth rates project higher final prices
        val momentumFactor = ln(1.0 + totalBids) * 0.1
        val historicalGrowthBonus = priceGrowthRate * remainingPercent * 0.5

        // Estimate additional growth based on time remaining, current momentum, and historical growth
        val additionalGrowthFactor = 1.0 + (momentumFactor * remainingPercent) + max(historicalGrowthBonus, 0.0)

        return currentPrice * additionalGrowthFactor
    }

    private fun generateBidReasoning(
        timeRemaining: Long,
        totalBids: Int
    ): String {
        val timeDesc = when {
            timeRemaining <= 60 -> "Auction ending in less than a minute"
            timeRemaining <= 300 -> "Less than 5 minutes remaining"
            timeRemaining <= 900 -> "About 15 minutes left"
            timeRemaining <= 3600 -> "Less than an hour remaining"
            timeRemaining <= 86400 -> "Auction ends within a day"
            else -> "Plenty of time remaining"
        }

        val competitionDesc = when {
            totalBids == 0 -> "No bids yet - opportunity to set the pace"
            totalBids <= 3 -> "Light bidding activity"
            totalBids <= 10 -> "Moderate competition"
            totalBids <= 25 -> "Active bidding - expect competition"
            else -> "Heavy competition - consider aggressive bids"
        }

        return "$timeDesc. $competitionDesc."
    }

    private fun generateRecommendation(
        marketHeat: MarketHeat,
        priceTrend: PriceTrend,
        remainingSec: Long,
        totalBids: Int
    ): String {
        return when {
            marketHeat == MarketHeat.VERY_HIGH && remainingSec < 300 ->
                "High activity in final minutes. Place your maximum bid soon to secure the item."
            marketHeat == MarketHeat.VERY_HIGH ->
                "Very active auction with strong demand. Consider setting a firm maximum and bidding strategically."
            marketHeat == MarketHeat.HIGH && priceTrend == PriceTrend.RISING_FAST ->
                "Price rising quickly. Decide your maximum value and be prepared to act fast."
            marketHeat == MarketHeat.HIGH ->
                "Good interest in this item. Monitor closely and bid when you're ready."
            marketHeat == MarketHeat.MEDIUM ->
                "Moderate activity. A well-timed bid could secure the item at a fair price."
            totalBids == 0 ->
                "No bids yet. You could secure this item at the starting price."
            else ->
                "Light activity so far. Consider waiting to see how bidding develops."
        }
    }

    private fun generateActivitySummary(
        totalBids: Int,
        elapsedSec: Long,
        currentPrice: Double,
        startingPrice: Double
    ): String {
        val elapsedHours = elapsedSec / 3600.0
        val priceIncrease = if (startingPrice > 0) ((currentPrice - startingPrice) / startingPrice) * 100 else 0.0

        return when {
            totalBids == 0 ->
                "No bids have been placed yet."
            elapsedHours < 1 ->
                "$totalBids bid(s) in the first hour. Price up ${priceIncrease.toInt()}% from start."
            else ->
                "$totalBids total bids (${(totalBids / elapsedHours).toInt()} per hour). Price has increased ${priceIncrease.toInt()}% from the starting price."
        }
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return Math.round(value * 100.0) / 100.0
    }
}
