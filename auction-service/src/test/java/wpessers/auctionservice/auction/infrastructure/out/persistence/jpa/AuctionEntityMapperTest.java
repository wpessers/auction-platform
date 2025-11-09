package wpessers.auctionservice.auction.infrastructure.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.shared.domain.Money;

class AuctionEntityMapperTest {

    private final AuctionEntityMapper mapper = new AuctionEntityMapper();

    @Test
    @DisplayName("Should map domain object to jpa entity")
    void shouldMapDomainToEntity() {
        UUID auctionId = UUID.randomUUID();
        Auction auction = new Auction(
            auctionId,
            "Charizard Holo",
            "Holographic Charizard card",
            new AuctionWindow(
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z")
            ),
            new Money(BigDecimal.ONE),
            AuctionStatus.ACTIVE
        );

        AuctionEntity entity = mapper.toEntity(auction);

        assertThat(entity.getId()).isEqualTo(auctionId);
        assertThat(entity.getName()).isEqualTo("Charizard Holo");
        assertThat(entity.getDescription()).isEqualTo("Holographic Charizard card");
        assertThat(entity.getStartTime()).isEqualTo(Instant.parse("2025-01-01T00:00:00Z"));
        assertThat(entity.getEndTime()).isEqualTo(Instant.parse("2025-01-02T00:00:00Z"));
        assertThat(entity.getStartingPrice()).isEqualTo(
            BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP));
        assertThat(entity.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should map domain object to jpa entity")
    void shouldMapEntityToDomain() {
        UUID auctionId = UUID.randomUUID();
        AuctionEntity auctionEntity = new AuctionEntity();
        auctionEntity.setId(auctionId);
        auctionEntity.setName("Charizard Holo");
        auctionEntity.setDescription("Holographic Charizard card");
        auctionEntity.setStartTime(Instant.parse("2025-01-01T00:00:00Z"));
        auctionEntity.setEndTime(Instant.parse("2025-01-02T00:00:00Z"));
        auctionEntity.setStartingPrice(BigDecimal.ONE);
        auctionEntity.setStatus(AuctionStatus.ACTIVE);

        Auction domain = mapper.toDomain(auctionEntity);

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