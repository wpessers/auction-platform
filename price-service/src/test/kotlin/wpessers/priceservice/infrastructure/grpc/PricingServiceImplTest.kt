package wpessers.priceservice.infrastructure.grpc

import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import wpessers.priceservice.grpc.*

class PricingServiceImplTest {

    private lateinit var pricingService: PricingServiceImpl

    @BeforeEach
    fun setUp() {
        pricingService = PricingServiceImpl()
    }

    @Test
    fun `getBidSuggestion returns valid suggestions for auction with no bids`() {
        val request = BidSuggestionRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setCurrentHighestBid(0.0)
            .setStartingPrice(100.0)
            .setTimeRemainingSeconds(3600)
            .setTotalBids(0)
            .build()

        @Suppress("UNCHECKED_CAST")
        val responseObserver = mock(StreamObserver::class.java) as StreamObserver<BidSuggestionResponse>
        pricingService.getBidSuggestion(request, responseObserver)

        val captor = org.mockito.ArgumentCaptor.forClass(BidSuggestionResponse::class.java)
        verify(responseObserver).onNext(captor.capture())
        verify(responseObserver).onCompleted()

        val response = captor.value
        assertTrue(response.conservativeBid > 100.0, "Conservative bid should be greater than starting price")
        assertTrue(response.moderateBid > response.conservativeBid, "Moderate bid should be greater than conservative")
        assertTrue(response.aggressiveBid > response.moderateBid, "Aggressive bid should be greater than moderate")
        assertTrue(response.reasoning.isNotBlank(), "Reasoning should not be blank")
    }

    @Test
    fun `getBidSuggestion returns higher suggestions when time is running out`() {
        val requestWithTime = BidSuggestionRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setCurrentHighestBid(100.0)
            .setStartingPrice(50.0)
            .setTimeRemainingSeconds(60) // Only 1 minute left - urgent
            .setTotalBids(5)
            .build()

        val requestWithMoreTime = BidSuggestionRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setCurrentHighestBid(100.0)
            .setStartingPrice(50.0)
            .setTimeRemainingSeconds(86400) // 1 day left - not urgent
            .setTotalBids(5)
            .build()

        @Suppress("UNCHECKED_CAST")
        val observer1 = mock(StreamObserver::class.java) as StreamObserver<BidSuggestionResponse>
        @Suppress("UNCHECKED_CAST")
        val observer2 = mock(StreamObserver::class.java) as StreamObserver<BidSuggestionResponse>

        pricingService.getBidSuggestion(requestWithTime, observer1)
        pricingService.getBidSuggestion(requestWithMoreTime, observer2)

        val captor = org.mockito.ArgumentCaptor.forClass(BidSuggestionResponse::class.java)
        verify(observer1).onNext(captor.capture())
        val urgentResponse = captor.value

        verify(observer2).onNext(captor.capture())
        val notUrgentResponse = captor.value

        assertTrue(urgentResponse.conservativeBid >= notUrgentResponse.conservativeBid,
            "Urgent suggestions should be higher or equal")
    }

    @Test
    fun `analyzeAuction returns correct market heat for active auction`() {
        val currentTime = System.currentTimeMillis() / 1000
        val request = AuctionAnalysisRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setAuctionName("Test Auction")
            .setStartingPrice(100.0)
            .setCurrentHighestBid(150.0)
            .setStartTimeEpoch(currentTime - 3600) // Started 1 hour ago
            .setEndTimeEpoch(currentTime + 3600)   // Ends in 1 hour
            .setTotalBids(10)
            .build()

        @Suppress("UNCHECKED_CAST")
        val responseObserver = mock(StreamObserver::class.java) as StreamObserver<AuctionAnalysisResponse>
        pricingService.analyzeAuction(request, responseObserver)

        val captor = org.mockito.ArgumentCaptor.forClass(AuctionAnalysisResponse::class.java)
        verify(responseObserver).onNext(captor.capture())
        verify(responseObserver).onCompleted()

        val response = captor.value
        assertNotNull(response.marketHeat)
        assertTrue(response.estimatedFinalPrice >= 150.0, "Estimated final price should be at least current highest bid")
        assertTrue(response.recommendation.isNotBlank(), "Recommendation should not be blank")
        assertTrue(response.activitySummary.isNotBlank(), "Activity summary should not be blank")
    }

    @Test
    fun `analyzeAuction returns LOW market heat for auction with no bids`() {
        val currentTime = System.currentTimeMillis() / 1000
        val request = AuctionAnalysisRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setAuctionName("Test Auction")
            .setStartingPrice(100.0)
            .setCurrentHighestBid(0.0)
            .setStartTimeEpoch(currentTime - 3600)
            .setEndTimeEpoch(currentTime + 3600)
            .setTotalBids(0)
            .build()

        @Suppress("UNCHECKED_CAST")
        val responseObserver = mock(StreamObserver::class.java) as StreamObserver<AuctionAnalysisResponse>
        pricingService.analyzeAuction(request, responseObserver)

        val captor = org.mockito.ArgumentCaptor.forClass(AuctionAnalysisResponse::class.java)
        verify(responseObserver).onNext(captor.capture())

        val response = captor.value
        assertEquals(MarketHeat.LOW, response.marketHeat, "Market heat should be LOW for auction with no bids")
    }

    @Test
    fun `analyzeAuction returns VERY_HIGH market heat for very active auction`() {
        val currentTime = System.currentTimeMillis() / 1000
        val request = AuctionAnalysisRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setAuctionName("Hot Auction")
            .setStartingPrice(100.0)
            .setCurrentHighestBid(500.0)
            .setStartTimeEpoch(currentTime - 3600) // Started 1 hour ago
            .setEndTimeEpoch(currentTime + 3600)
            .setTotalBids(50) // 50 bids per hour = very high activity
            .build()

        @Suppress("UNCHECKED_CAST")
        val responseObserver = mock(StreamObserver::class.java) as StreamObserver<AuctionAnalysisResponse>
        pricingService.analyzeAuction(request, responseObserver)

        val captor = org.mockito.ArgumentCaptor.forClass(AuctionAnalysisResponse::class.java)
        verify(responseObserver).onNext(captor.capture())

        val response = captor.value
        assertEquals(MarketHeat.VERY_HIGH, response.marketHeat, "Market heat should be VERY_HIGH for very active auction")
    }

    @Test
    fun `getBidSuggestion rounds values to two decimal places`() {
        val request = BidSuggestionRequest.newBuilder()
            .setAuctionId("test-auction-id")
            .setCurrentHighestBid(99.99)
            .setStartingPrice(50.0)
            .setTimeRemainingSeconds(3600)
            .setTotalBids(3)
            .build()

        @Suppress("UNCHECKED_CAST")
        val responseObserver = mock(StreamObserver::class.java) as StreamObserver<BidSuggestionResponse>
        pricingService.getBidSuggestion(request, responseObserver)

        val captor = org.mockito.ArgumentCaptor.forClass(BidSuggestionResponse::class.java)
        verify(responseObserver).onNext(captor.capture())

        val response = captor.value
        // Check that values are rounded to 2 decimal places
        assertEquals(response.conservativeBid, (response.conservativeBid * 100).toLong() / 100.0, 0.001)
        assertEquals(response.moderateBid, (response.moderateBid * 100).toLong() / 100.0, 0.001)
        assertEquals(response.aggressiveBid, (response.aggressiveBid * 100).toLong() / 100.0, 0.001)
    }
}
