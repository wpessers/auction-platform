package wpessers.auctionservice.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.exception.InvalidAuctionWindowException;

class AuctionWindowTests {

    @Test
    @DisplayName("Should throw exception when end time is before start time")
    void shouldThrowOnEarlierEndTime() {
        // Given
        Instant startTime = Instant.parse("2025-01-02T01:00:00Z");
        Instant endTime = startTime.minusSeconds(1L);

        // When / Then
        assertThrows(InvalidAuctionWindowException.class,
            () -> new AuctionWindow(startTime, endTime));
    }

    @Test
    @DisplayName("Should throw exception when start and end time are the same")
    void shouldThrowOnEndTimeEqualToStartTime() {
        // Given
        Instant sameTime = Instant.parse("2025-01-02T01:00:00Z");

        // When / Then
        assertThrows(InvalidAuctionWindowException.class,
            () -> new AuctionWindow(sameTime, sameTime));
    }
}