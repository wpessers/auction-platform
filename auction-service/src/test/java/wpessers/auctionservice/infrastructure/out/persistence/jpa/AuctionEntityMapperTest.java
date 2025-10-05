package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;

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
            new Money(BigDecimal.ONE)
        );

        // When
        AuctionEntity entity = mapper.toEntity(auction);

        // Then
        assertThat(entity.getId()).isEqualTo(auctionId);
        assertThat(entity.getName()).isEqualTo("Charizard Holo");
        assertThat(entity.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(entity.getStartTime()).isEqualTo(Instant.parse("2025-01-01T00:00:00Z"));
        assertThat(entity.getEndTime()).isEqualTo(Instant.parse("2025-01-02T00:00:00Z"));
        assertThat(entity.getStartingPrice()).isEqualTo(BigDecimal.ONE.setScale(2));
        assertThat(entity.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should map domain object to jpa entity")
    void shouldMapEntityToDomain() {
        // Given
        UUID auctionId = UUID.randomUUID();
        AuctionEntity auctionEntity = new AuctionEntity();
        auctionEntity.setId(auctionId);
        auctionEntity.setName("Charizard Holo");
        auctionEntity.setDescription("Holographic Charizard card");
        auctionEntity.setStartTime(Instant.parse("2025-01-01T00:00:00Z"));
        auctionEntity.setEndTime(Instant.parse("2025-01-02T00:00:00Z"));
        auctionEntity.setStartingPrice(BigDecimal.ONE);
        auctionEntity.setStatus(AuctionStatus.ACTIVE);

        // When
        Auction domain = mapper.toDomain(auctionEntity);

        // Then
        assertThat(domain.getId()).isEqualTo(auctionId);
        assertThat(domain.getName()).isEqualTo("Charizard Holo");
        assertThat(domain.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(domain.getAuctionWindow()).isEqualTo(new AuctionWindow(
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2025-01-02T00:00:00Z")
        ));
        assertThat(domain.getStartingPrice()).isEqualTo(new Money(BigDecimal.ONE));
        assertThat(domain.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }
}