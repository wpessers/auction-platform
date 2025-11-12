package wpessers.auctionservice.bid.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.infrastructure.out.cache.inmemory.FakeAuctionRegistryAdapter;
import wpessers.auctionservice.bid.application.port.in.PlaceBidCommand;
import wpessers.auctionservice.bid.domain.event.BidPlacedEvent;
import wpessers.auctionservice.bid.infrastructure.out.FakeBidEventPublisherAdapter;
import wpessers.auctionservice.fixtures.AuctionBuilder;
import wpessers.auctionservice.shared.domain.Money;
import wpessers.auctionservice.shared.infrastructure.out.time.StubTimeProviderAdapter;

import static org.assertj.core.api.Assertions.assertThat;

class BidServiceTests {

    private static final Instant AUCTION_START = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant AUCTION_END = Instant.parse("2025-01-02T10:00:00Z");
    private static final UUID AUCTION_ID = UUID.randomUUID();

    private FakeAuctionRegistryAdapter auctionRegistry;
    private FakeBidEventPublisherAdapter eventPublisher;
    private StubTimeProviderAdapter timeProvider;
    private BidService bidService;

    @BeforeEach
    void setUp() {
        timeProvider = new StubTimeProviderAdapter();
        timeProvider.setFixedTime(AUCTION_START);

        auctionRegistry = new FakeAuctionRegistryAdapter();
        Auction auction = new AuctionBuilder()
            .withId(AUCTION_ID)
            .withTimeWindow(AUCTION_START, AUCTION_END).build();
        auctionRegistry.register(auction);

        eventPublisher = new FakeBidEventPublisherAdapter();
        bidService = new BidService(auctionRegistry, eventPublisher, timeProvider);
    }

    @Test
    @DisplayName("")
    void shouldCallPlaceBid() {
        UUID bidderId = UUID.randomUUID();
        PlaceBidCommand placeBidCommand = new PlaceBidCommand(bidderId, AUCTION_ID,
            BigDecimal.ONE);

        bidService.placeBid(placeBidCommand);

        assertThat(eventPublisher.getPlacedEvents()).isEqualTo(
            List.of(new BidPlacedEvent(AUCTION_ID, bidderId, null, new Money(BigDecimal.ONE))));
        assertThat(eventPublisher.getRejectedEvents()).isEmpty();
    }
}