package wpessers.auctionservice.bid.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.infrastructure.out.cache.inmemory.InMemoryAuctionRegistry;
import wpessers.auctionservice.bid.application.port.in.PlaceBidCommand;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.domain.event.BidRejectedEvent;
import wpessers.auctionservice.bid.domain.event.RejectionReason;
import wpessers.auctionservice.bid.infrastructure.out.FakeBidEventPublisherAdapter;
import wpessers.auctionservice.bid.infrastructure.out.persistence.inmemory.FakeBidStorageAdapter;
import wpessers.auctionservice.fixtures.AuctionBuilder;
import wpessers.auctionservice.shared.domain.Money;
import wpessers.auctionservice.shared.infrastructure.out.time.StubTimeProviderAdapter;

class BidServiceTests {

    private static final Instant CURRENT_TIME = Instant.parse("2025-01-01T10:00:00Z");

    private static final UUID ACTIVE_AUCTION_ID = UUID.fromString(
        "00000000-0000-0000-0000-000000000000");
    private static final Instant ACTIVE_AUCTION_START = CURRENT_TIME;
    private static final Instant ACTIVE_AUCTION_END = CURRENT_TIME.plus(1L, ChronoUnit.DAYS);

    private static final UUID CLOSED_AUCTION_ID = UUID.fromString(
        "11111111-1111-1111-1111-111111111111");
    private static final Instant CLOSED_AUCTION_START = CURRENT_TIME.minus(2L, ChronoUnit.DAYS);
    private static final Instant CLOSED_AUCTION_END = CURRENT_TIME.minus(1L, ChronoUnit.DAYS);

    private InMemoryAuctionRegistry auctionRegistry;
    private FakeBidStorageAdapter bidStorage;
    private FakeBidEventPublisherAdapter eventPublisher;
    private StubTimeProviderAdapter timeProvider;
    private BidService bidService;

    @BeforeEach
    void setUp() {
        timeProvider = new StubTimeProviderAdapter();
        timeProvider.setFixedTime(CURRENT_TIME);

        auctionRegistry = new InMemoryAuctionRegistry();
        Auction activeAuction = new AuctionBuilder()
            .withId(ACTIVE_AUCTION_ID)
            .withTimeWindow(ACTIVE_AUCTION_START, ACTIVE_AUCTION_END)
            .build();
        auctionRegistry.register(activeAuction);
        Auction closedAuction = new AuctionBuilder()
            .withId(CLOSED_AUCTION_ID)
            .withTimeWindow(CLOSED_AUCTION_START, CLOSED_AUCTION_END)
            .build();
        auctionRegistry.register(closedAuction);

        bidStorage = new FakeBidStorageAdapter();
        eventPublisher = new FakeBidEventPublisherAdapter();
        bidService = new BidService(auctionRegistry, bidStorage, eventPublisher, timeProvider);
    }

    @Test
    @DisplayName("Should place a bid on an active auction")
    void shouldPlaceBid() {
        UUID bidderId = UUID.randomUUID();
        PlaceBidCommand placeBidCommand = new PlaceBidCommand(bidderId, ACTIVE_AUCTION_ID,
            BigDecimal.ONE);

        bidService.placeBid(placeBidCommand);

        assertThat(eventPublisher.getPlacedEvents()).isEqualTo(List.of(
            new BidPlacedEvent(ACTIVE_AUCTION_ID, bidderId, null, new Money(BigDecimal.ONE))));
        assertThat(eventPublisher.getRejectedEvents()).isEmpty();
        assertThat(bidStorage.getBids()).hasSize(1);
    }

    @Test
    @DisplayName("Should send rejected event when trying to place bid on a closed auction")
    void shouldRejectBidOnClosedAuction() {
        UUID bidderId = UUID.randomUUID();
        PlaceBidCommand placeBidCommand = new PlaceBidCommand(bidderId, CLOSED_AUCTION_ID,
            BigDecimal.ONE);

        bidService.placeBid(placeBidCommand);

        assertThat(eventPublisher.getPlacedEvents()).isEmpty();
        assertThat(eventPublisher.getRejectedEvents()).isEqualTo(List.of(
            new BidRejectedEvent(CLOSED_AUCTION_ID, bidderId, new Money(BigDecimal.ONE),
                RejectionReason.AUCTION_CLOSED)));
    }

    @Test
    @DisplayName("Should send rejected event when trying to place bid on a closed auction")
    void shouldRejectBidOnBidTooLow() {
        UUID firstBidderId = UUID.randomUUID();
        PlaceBidCommand placeBidCommand = new PlaceBidCommand(firstBidderId, ACTIVE_AUCTION_ID,
            BigDecimal.TWO);
        bidService.placeBid(placeBidCommand);

        UUID bidderId = UUID.randomUUID();
        PlaceBidCommand lowBidCommand = new PlaceBidCommand(bidderId, ACTIVE_AUCTION_ID,
            BigDecimal.ONE);
        bidService.placeBid(lowBidCommand);

        assertThat(eventPublisher.getPlacedEvents()).hasSize(1);
        assertThat(eventPublisher.getRejectedEvents()).isEqualTo(List.of(
            new BidRejectedEvent(ACTIVE_AUCTION_ID, bidderId, new Money(BigDecimal.ONE),
                RejectionReason.BID_TOO_LOW)));
    }
}