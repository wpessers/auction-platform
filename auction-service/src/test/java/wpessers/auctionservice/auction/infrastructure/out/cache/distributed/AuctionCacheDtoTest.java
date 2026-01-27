package wpessers.auctionservice.auction.infrastructure.out.cache.distributed;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.shared.domain.Money;

class AuctionCacheDtoTest {

    @Test
    @DisplayName("Should correctly convert auction to DTO and back with full state")
    void shouldRoundTripAuctionWithFullState() {
        UUID id = UUID.randomUUID();
        UUID winnerId = UUID.randomUUID();
        AuctionWindow window = new AuctionWindow(
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T20:00:00Z")
        );

        Auction original = Auction.reconstruct(
            id, "Test Auction", "Description", window,
            new Money(100), AuctionStatus.ACTIVE,
            new Money(150), winnerId, 5L
        );

        AuctionCacheDto dto = AuctionCacheDto.fromAuction(original);
        Auction restored = dto.toAuction();

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getName()).isEqualTo(original.getName());
        assertThat(restored.getDescription()).isEqualTo(original.getDescription());
        assertThat(restored.getAuctionWindow()).isEqualTo(original.getAuctionWindow());
        assertThat(restored.getStartingPrice()).isEqualTo(original.getStartingPrice());
        assertThat(restored.getStatus()).isEqualTo(original.getStatus());
        assertThat(restored.getHighestBid()).isEqualTo(original.getHighestBid());
        assertThat(restored.getCurrentWinnerId()).isEqualTo(original.getCurrentWinnerId());
        assertThat(restored.getBidVersion()).isEqualTo(original.getBidVersion());
    }

    @Test
    @DisplayName("Should correctly convert auction to DTO and back with no bids")
    void shouldRoundTripAuctionWithNoBids() {
        UUID id = UUID.randomUUID();
        AuctionWindow window = new AuctionWindow(
            Instant.parse("2025-01-01T10:00:00Z"),
            Instant.parse("2025-01-01T20:00:00Z")
        );

        Auction original = new Auction(
            id, "Test Auction", "Description", window,
            new Money(100), AuctionStatus.SCHEDULED
        );

        AuctionCacheDto dto = AuctionCacheDto.fromAuction(original);
        Auction restored = dto.toAuction();

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getHighestBid()).isNull();
        assertThat(restored.getCurrentWinnerId()).isNull();
        assertThat(restored.getBidVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("DTO should preserve all fields correctly")
    void shouldPreserveAllFieldsInDto() {
        UUID id = UUID.randomUUID();
        UUID winnerId = UUID.randomUUID();
        Instant startTime = Instant.parse("2025-01-01T10:00:00Z");
        Instant endTime = Instant.parse("2025-01-01T20:00:00Z");
        AuctionWindow window = new AuctionWindow(startTime, endTime);

        Auction auction = Auction.reconstruct(
            id, "Test", "Desc", window,
            new Money(100), AuctionStatus.ACTIVE,
            new Money(150), winnerId, 3L
        );

        AuctionCacheDto dto = AuctionCacheDto.fromAuction(auction);

        assertThat(dto.getId()).isEqualTo(id.toString());
        assertThat(dto.getName()).isEqualTo("Test");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getStartTime()).isEqualTo(startTime.toString());
        assertThat(dto.getEndTime()).isEqualTo(endTime.toString());
        assertThat(dto.getStartingPrice()).isEqualTo("100.00");
        assertThat(dto.getStatus()).isEqualTo("ACTIVE");
        assertThat(dto.getHighestBid()).isEqualTo("150.00");
        assertThat(dto.getCurrentWinnerId()).isEqualTo(winnerId.toString());
        assertThat(dto.getBidVersion()).isEqualTo(3L);
    }
}
