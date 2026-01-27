package wpessers.auctionservice.auction.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.auction.infrastructure.out.persistence.jpa.AuctionEntity;
import wpessers.auctionservice.auction.infrastructure.out.persistence.jpa.AuctionEntityMapper;
import wpessers.auctionservice.auction.infrastructure.out.persistence.jpa.AuctionRepository;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;
import wpessers.auctionservice.shared.domain.Money;

@ExtendWith(MockitoExtension.class)
class AuctionLifecycleSchedulerTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionEntityMapper entityMapper;

    @Mock
    private AuctionRegistry auctionRegistry;

    @Mock
    private AuctionStorage auctionStorage;

    @Mock
    private TimeProvider timeProvider;

    private AuctionLifecycleScheduler scheduler;

    private static final Instant NOW = Instant.parse("2026-01-27T10:00:00Z");

    @BeforeEach
    void setUp() {
        scheduler = new AuctionLifecycleScheduler(
            auctionRepository,
            entityMapper,
            auctionRegistry,
            auctionStorage,
            timeProvider
        );
        when(timeProvider.now()).thenReturn(NOW);
    }

    @Test
    @DisplayName("Should activate scheduled auctions when start time is reached")
    void shouldActivateScheduledAuctions() {
        UUID auctionId = UUID.randomUUID();
        AuctionEntity entity = createAuctionEntity(auctionId, AuctionStatus.SCHEDULED);
        Auction auction = createAuction(auctionId, AuctionStatus.SCHEDULED);

        when(auctionRepository.findByStatusAndStartTimeLessThanEqual(AuctionStatus.SCHEDULED, NOW))
            .thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(auction);

        scheduler.activateScheduledAuctions();

        verify(auctionStorage).save(auction);
        verify(auctionRegistry).register(auction);
    }

    @Test
    @DisplayName("Should not activate when no scheduled auctions exist")
    void shouldNotActivateWhenNoScheduledAuctions() {
        when(auctionRepository.findByStatusAndStartTimeLessThanEqual(AuctionStatus.SCHEDULED, NOW))
            .thenReturn(List.of());

        scheduler.activateScheduledAuctions();

        verify(auctionStorage, never()).save(any());
        verify(auctionRegistry, never()).register(any());
    }

    @Test
    @DisplayName("Should close active auctions when end time is reached")
    @SuppressWarnings("unchecked")
    void shouldCloseExpiredAuctions() {
        UUID auctionId = UUID.randomUUID();
        AuctionEntity entity = createAuctionEntity(auctionId, AuctionStatus.ACTIVE);
        Auction auction = createAuction(auctionId, AuctionStatus.ACTIVE);

        when(auctionRepository.findByStatusAndEndTimeLessThanEqual(AuctionStatus.ACTIVE, NOW))
            .thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(auction);
        when(auctionRegistry.executeOnAuction(eq(auctionId), any(Function.class)))
            .thenAnswer(invocation -> {
                Function<Auction, Void> action = invocation.getArgument(1);
                return action.apply(auction);
            });

        scheduler.closeExpiredAuctions();

        verify(auctionRegistry).executeOnAuction(eq(auctionId), any(Function.class));
        verify(auctionRegistry).deregister(auctionId);
    }

    @Test
    @DisplayName("Should not close when no expired auctions exist")
    void shouldNotCloseWhenNoExpiredAuctions() {
        when(auctionRepository.findByStatusAndEndTimeLessThanEqual(AuctionStatus.ACTIVE, NOW))
            .thenReturn(List.of());

        scheduler.closeExpiredAuctions();

        verify(auctionRegistry, never()).executeOnAuction(any(), any());
        verify(auctionRegistry, never()).deregister(any());
    }

    private AuctionEntity createAuctionEntity(UUID id, AuctionStatus status) {
        AuctionEntity entity = new AuctionEntity();
        entity.setId(id);
        entity.setName("Test Auction");
        entity.setDescription("Test Description");
        entity.setStartTime(NOW.minusSeconds(3600));
        entity.setEndTime(NOW.minusSeconds(60));
        entity.setStartingPrice(BigDecimal.valueOf(100));
        entity.setStatus(status);
        return entity;
    }

    private Auction createAuction(UUID id, AuctionStatus status) {
        return new Auction(
            id,
            "Test Auction",
            "Test Description",
            new AuctionWindow(NOW.minusSeconds(3600), NOW.plusSeconds(3600)),
            new Money(BigDecimal.valueOf(100)),
            status
        );
    }
}
