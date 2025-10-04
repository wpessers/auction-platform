package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionEntityMapperTest {

    private final AuctionEntityMapper mapper = new AuctionEntityMapper();

    @Test
    @DisplayName("Should map domain object to jpa entity")
    void shouldMapDomainToEntity() {
        // Given
        UUID auctionId = UUID.randomUUID();
        Auction auction = Auction.create(
            auctionId,
            "Charizard Holo",
            "Holographic Charizard card",
            new AuctionWindow(
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z")
            ),
            new Money(100)
        );

        // When
        AuctionEntity entity = mapper.toEntity(auction);

        // Then
        assertThat(entity.getId()).isEqualTo(auctionId);
        assertThat(entity.getName()).isEqualTo("Charizard Holo");
        assertThat(entity.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(entity.getStartTime()).isEqualTo(Instant.parse("2025-01-01T00:00:00Z"));
        assertThat(entity.getEndTime()).isEqualTo(Instant.parse("2025-01-02T00:00:00Z"));
        assertThat(entity.getStartingPrice().doubleValue()).isEqualTo(100);
        assertThat(entity.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }
}