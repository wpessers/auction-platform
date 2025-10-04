package wpessers.auctionservice.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;
import wpessers.auctionservice.infrastructure.out.generation.fixed.StubAuctionIdGeneratorAdapter;
import wpessers.auctionservice.infrastructure.out.persistence.inmemory.FakeAuctionStorageAdapter;
import wpessers.auctionservice.infrastructure.out.time.fixed.StubTimeProviderAdapter;


class AuctionServiceTests {

    private StubAuctionIdGeneratorAdapter idGenerator;
    private FakeAuctionStorageAdapter auctionStorage;
    private StubTimeProviderAdapter timeProvider;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        this.idGenerator = new StubAuctionIdGeneratorAdapter();
        this.auctionStorage = new FakeAuctionStorageAdapter();
        this.timeProvider = new StubTimeProviderAdapter();
        this.auctionService = new AuctionService(idGenerator, auctionStorage, timeProvider);
    }

    @Test
    @DisplayName("Should create auction with fields given in command")
    void shouldCreateAuction() {
        // Given
        UUID auctionId = UUID.randomUUID();
        idGenerator.addId(auctionId);

        Instant startTime = Instant.parse("2025-01-01T10:00:00Z");
        timeProvider.setFixedTime(startTime);
        Instant endTime = startTime.plusSeconds(60);

        CreateAuctionCommand command = new CreateAuctionCommand(
            "Charizard Holo",
            "Holographic Charizard card",
            endTime,
            100
        );

        // When
        UUID actualId = auctionService.createAuction(command);

        // Then
        assertThat(actualId).isEqualTo(auctionId);
        Auction actualAuction = auctionStorage.get(actualId);
        assertThat(actualAuction.getName()).isEqualTo("Charizard Holo");
        assertThat(actualAuction.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(actualAuction.getAuctionWindow()).isEqualTo(new AuctionWindow(startTime, endTime));
        assertThat(actualAuction.getStartingPrice()).isEqualTo(new Money(100));
    }
}