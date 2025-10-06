package wpessers.auctionservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.in.query.AuctionResponse;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;
import wpessers.auctionservice.domain.exception.AuctionNotFoundException;
import wpessers.auctionservice.fixtures.AuctionBuilder;
import wpessers.auctionservice.fixtures.CreateAuctionCommandBuilder;
import wpessers.auctionservice.infrastructure.out.generation.fixed.StubAuctionIdGeneratorAdapter;
import wpessers.auctionservice.infrastructure.out.persistence.inmemory.FakeAuctionStorageAdapter;
import wpessers.auctionservice.infrastructure.out.time.fixed.StubTimeProviderAdapter;


class AuctionServiceTests {

    private StubAuctionIdGeneratorAdapter idGenerator;
    private FakeAuctionStorageAdapter auctionStorage;
    private StubTimeProviderAdapter timeProvider;
    private AuctionService auctionService;

    private final AuctionMapper mapper = new AuctionMapper();

    @BeforeEach
    void setUp() {
        this.idGenerator = new StubAuctionIdGeneratorAdapter();
        this.auctionStorage = new FakeAuctionStorageAdapter();
        this.timeProvider = new StubTimeProviderAdapter();
        this.auctionService = new AuctionService(idGenerator, auctionStorage, timeProvider, mapper);
    }

    @Test
    @DisplayName("Should create open auction when no start time is given, with details given in command")
    void shouldCreateAuction() {
        // Given
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
            .withStartingPrice(100)
            .build();

        // When
        UUID actualId = auctionService.createAuction(command);

        // Then
        assertThat(actualId).isEqualTo(auctionId);
        Auction actualAuction = auctionStorage.get(actualId);
        assertThat(actualAuction.getName()).isEqualTo("Charizard Holo");
        assertThat(actualAuction.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(actualAuction.getAuctionWindow()).isEqualTo(
            new AuctionWindow(currentTime, endTime)
        );
        assertThat(actualAuction.getStartingPrice()).isEqualTo(new Money(100));
        assertThat(actualAuction.getStatus()).isEqualTo(AuctionStatus.OPEN);
    }

    @Test
    @DisplayName("Should create scheduled auction when start time is in the future")
    void shouldCreateScheduledAuction() {
        // Given
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

        // When
        UUID actualId = auctionService.createAuction(command);

        // Then
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
        // Given
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

        // When
        UUID actualId = auctionService.createAuction(command);

        // Then
        assertThat(actualId).isEqualTo(auctionId);
        Auction actualAuction = auctionStorage.get(actualId);
        assertThat(actualAuction.getAuctionWindow()).isEqualTo(
            new AuctionWindow(currentTime, endTime)
        );
        assertThat(actualAuction.getStatus()).isEqualTo(AuctionStatus.OPEN);
    }

    @Test
    @DisplayName("Should return auction details")
    void shouldReturnAuctionDetails() {
        // Given
        UUID auctionId = UUID.randomUUID();
        Auction auction = new AuctionBuilder().withId(auctionId).build();
        auctionStorage.save(auction);

        // When
        AuctionResponse auctionResponse = auctionService.findAuction(auctionId);

        // Then
        assertThat(auctionResponse.id()).isEqualTo(auctionId);
    }

    @Test
    @DisplayName("Should throw exception when auction not found")
    void shouldThrowOnAuctionNotFound() {
        // Given
        UUID nonExistentAuctionId = UUID.randomUUID();

        // When / Then
        assertThrows(AuctionNotFoundException.class,
            () -> auctionService.findAuction(nonExistentAuctionId));
    }

    @Test
    @DisplayName("Should return active auctions")
    void shouldReturnActiveAuctions() {
        // Given
        UUID activeAuctionId1 = UUID.randomUUID();
        UUID activeAuctionId2 = UUID.randomUUID();
        UUID endedAuctionId = UUID.randomUUID();

        Auction activeAuction1 = new AuctionBuilder()
            .withId(activeAuctionId1)
            .withStatus(AuctionStatus.OPEN)
            .build();
        Auction activeAuction2 = new AuctionBuilder()
            .withId(activeAuctionId2)
            .withStatus(AuctionStatus.OPEN)
            .build();
        Auction endedAuction = new AuctionBuilder()
            .withId(endedAuctionId)
            .withStatus(AuctionStatus.CLOSED)
            .build();

        auctionStorage.save(activeAuction1);
        auctionStorage.save(activeAuction2);
        auctionStorage.save(endedAuction);

        // When
        List<AuctionResponse> auctions = auctionService.getActiveAuctions();

        // Then
        assertThat(auctions).hasSize(2);
        assertThat(auctions).extracting(AuctionResponse::id)
            .containsExactlyInAnyOrder(activeAuctionId1, activeAuctionId2);
    }

}