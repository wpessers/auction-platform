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
        Instant fixedStartTime = Instant.parse("2025-01-01T10:00:00Z");
        timeProvider.setFixedTime(fixedStartTime);
        Instant auctionEndTime = fixedStartTime.plusSeconds(60);

        String auctionName = "Mona Lisa";
        String auctionDescription = "16th Century painting by Leonardo da Vinci";
        int auctionStartingPrice = 10;

        CreateAuctionCommand command = new CreateAuctionCommand(
            auctionName,
            auctionDescription,
            auctionEndTime,
            auctionStartingPrice
        );

        Auction expectedAuction = Auction.create(
            auctionId,
            auctionName,
            auctionDescription,
            new AuctionWindow(fixedStartTime, auctionEndTime),
            new Money(auctionStartingPrice)
        );

        // When
        UUID actualId = auctionService.createAuction(command);

        // Then
        assertThat(actualId).isEqualTo(auctionId);
        assertThat(auctionStorage.get(actualId)).usingRecursiveComparison().isEqualTo(expectedAuction);
    }
}