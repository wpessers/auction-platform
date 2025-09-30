package wpessers.auctionservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.exception.InvalidEndTimeException;
import wpessers.auctionservice.domain.model.Auction;
import java.time.Instant;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuctionTests {

    @Test
    @DisplayName("Should default end time to 5 days after start time, if no end time is given")
    void shouldSetDefaultEndTime() {
        // Given
        Instant startTime = Instant.parse("2025-01-01T01:00:00Z");

        // When
        Auction auction = Auction.create(
            randomUUID(),
            "Mona Lisa",
            "16th Century painting by Leonardo da Vinci",
            startTime,
            null
        );

        // Then
        assertThat(auction).isNotNull();
        Instant expectedEndTime = startTime.plus(5, DAYS);
        assertThat(auction.getEndTime()).isEqualTo(expectedEndTime);
    }

    @Test
    @DisplayName("Should throw exception when end time is before start time")
    void shouldThrowOnInvalidEndTime() {
        // Given
        UUID id = randomUUID();
        String name = "Mona Lisa";
        String description = "16th Century painting by Leonardo da Vinci";
        Instant startTime = Instant.parse("2025-01-02T01:00:00Z");
        Instant endTime = Instant.parse("2025-01-01T01:00:00Z");

        // When / Then
        assertThrows(InvalidEndTimeException.class, () ->
            Auction.create(id, name, description, startTime, endTime)
        );
    }
}