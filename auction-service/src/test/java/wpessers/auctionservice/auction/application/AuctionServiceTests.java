package wpessers.auctionservice.auction.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.application.port.in.AuctionResponse;
import wpessers.auctionservice.auction.application.port.in.CreateAuctionCommand;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.auction.domain.exception.AuctionNotFoundException;
import wpessers.auctionservice.auction.infrastructure.out.cache.inmemory.FakeAuctionRegistryAdapter;
import wpessers.auctionservice.auction.infrastructure.out.persistence.inmemory.FakeAuctionStorageAdapter;
import wpessers.auctionservice.fixtures.AuctionBuilder;
import wpessers.auctionservice.fixtures.CreateAuctionCommandBuilder;
import wpessers.auctionservice.shared.domain.Money;
import wpessers.auctionservice.shared.infrastructure.out.generation.StubIdGeneratorAdapter;
import wpessers.auctionservice.shared.infrastructure.out.time.StubTimeProviderAdapter;


class AuctionServiceTests {

    private StubIdGeneratorAdapter idGenerator;
    private FakeAuctionStorageAdapter auctionStorage;
    private FakeAuctionRegistryAdapter auctionRegistry;
    private StubTimeProviderAdapter timeProvider;
    private final AuctionMapper mapper = new AuctionMapper();

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        this.idGenerator = new StubIdGeneratorAdapter();
        this.auctionStorage = new FakeAuctionStorageAdapter();
        this.timeProvider = new StubTimeProviderAdapter();
        this.auctionRegistry = new FakeAuctionRegistryAdapter();
        this.auctionService = new AuctionService(idGenerator, auctionStorage, auctionRegistry,
            timeProvider, mapper);
    }

    @Test
    @DisplayName("Should create open auction when no start time is given, with details given in command")
    void shouldCreateAuction() {
        UUID auctionId = UUID.randomUUID();
        idGenerator.addId(auctionId);

        Instant currentTime = Instant.parse("2025-01-01T10:00:00Z");
        timeProvider.setFixedTime(currentTime);
        Instant endTime = currentTime.plusSeconds(60);

        CreateAuctionCommand command = new CreateAuctionCommandBuilder()
            .withName("Charizard Holo")
            .withDescription("Holographic Charizard card")
            .withStartTime(null)
            .withEndTime(endTime)
            .withStartingPrice(BigDecimal.ONE)
            .build();

        UUID actualId = auctionService.createAuction(command);

        assertThat(actualId).isEqualTo(auctionId);
        assertThat(auctionRegistry.isRegistered(actualId)).isTrue();
        Auction actualAuction = auctionStorage.get(actualId);
        assertThat(actualAuction.getName()).isEqualTo("Charizard Holo");
        assertThat(actualAuction.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(actualAuction.getAuctionWindow()).isEqualTo(
            new AuctionWindow(currentTime, endTime)
        );
        assertThat(actualAuction.getStartingPrice()).isEqualTo(new Money(1));
        assertThat(actualAuction.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should create scheduled auction when start time is in the future")
    void shouldCreateScheduledAuction() {
        UUID auctionId = UUID.randomUUID();
        idGenerator.addId(auctionId);

        Instant currentTime = Instant.parse("2025-01-01T10:00:00Z");
        timeProvider.setFixedTime(currentTime);
        Instant startTime = currentTime.plusSeconds(30);
        Instant endTime = currentTime.plusSeconds(60);

        CreateAuctionCommand command = new CreateAuctionCommandBuilder()
            .withStartTime(startTime)
            .withEndTime(endTime)
            .build();

        UUID actualId = auctionService.createAuction(command);

        assertThat(actualId).isEqualTo(auctionId);
        Auction actualAuction = auctionStorage.get(actualId);
        assertThat(actualAuction.getAuctionWindow()).isEqualTo(
            new AuctionWindow(startTime, endTime)
        );
        assertThat(actualAuction.getStatus()).isEqualTo(AuctionStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Should create open auction with start time now when given start time is in the past")
    void shouldDefaultPastStartTime() {
        UUID auctionId = UUID.randomUUID();
        idGenerator.addId(auctionId);

        Instant currentTime = Instant.parse("2025-01-01T10:00:00Z");
        timeProvider.setFixedTime(currentTime);
        Instant startTime = currentTime.minusSeconds(60);
        Instant endTime = currentTime.plusSeconds(60);

        CreateAuctionCommand command = new CreateAuctionCommandBuilder()
            .withStartTime(startTime)
            .withEndTime(endTime)
            .build();

        UUID actualId = auctionService.createAuction(command);

        assertThat(actualId).isEqualTo(auctionId);
        Auction actualAuction = auctionStorage.get(actualId);
        assertThat(actualAuction.getAuctionWindow()).isEqualTo(
            new AuctionWindow(currentTime, endTime)
        );
        assertThat(actualAuction.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return auction details")
    void shouldReturnAuctionDetails() {
        UUID auctionId = UUID.randomUUID();
        Auction auction = new AuctionBuilder().withId(auctionId).build();
        auctionStorage.save(auction);

        AuctionResponse auctionResponse = auctionService.findAuction(auctionId);

        assertThat(auctionResponse.id()).isEqualTo(auctionId);
    }

    @Test
    @DisplayName("Should throw exception when auction not found")
    void shouldThrowOnAuctionNotFound() {
        UUID nonExistentAuctionId = UUID.randomUUID();

        assertThrows(AuctionNotFoundException.class,
            () -> auctionService.findAuction(nonExistentAuctionId));
    }

    @Test
    @DisplayName("Should return active auctions")
    void shouldReturnActiveAuctions() {
        UUID activeAuctionId1 = UUID.randomUUID();
        UUID activeAuctionId2 = UUID.randomUUID();
        UUID endedAuctionId = UUID.randomUUID();

        Auction activeAuction1 = new AuctionBuilder()
            .withId(activeAuctionId1)
            .withStatus(AuctionStatus.ACTIVE)
            .build();
        Auction activeAuction2 = new AuctionBuilder()
            .withId(activeAuctionId2)
            .withStatus(AuctionStatus.ACTIVE)
            .build();
        Auction endedAuction = new AuctionBuilder()
            .withId(endedAuctionId)
            .withStatus(AuctionStatus.CLOSED)
            .build();

        auctionStorage.save(activeAuction1);
        auctionStorage.save(activeAuction2);
        auctionStorage.save(endedAuction);

        List<AuctionResponse> auctions = auctionService.getActiveAuctions();

        assertThat(auctions).hasSize(2);
        assertThat(auctions).extracting(AuctionResponse::id)
            .containsExactlyInAnyOrder(activeAuctionId1, activeAuctionId2);
    }
}