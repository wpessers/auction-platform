package wpessers.auctionservice.auction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.exception.InvalidAuctionWindowException;

class AuctionWindowTests {

    @Test
    @DisplayName("Should throw exception when end time is before start time")
    void shouldThrowOnEarlierEndTime() {
        Instant startTime = Instant.parse("2025-01-02T01:00:00Z");
        Instant endTime = startTime.minusSeconds(1L);

        assertThrows(InvalidAuctionWindowException.class,
            () -> new AuctionWindow(startTime, endTime));
    }

    @Test
    @DisplayName("Should throw exception when start and end time are the same")
    void shouldThrowOnEndTimeEqualToStartTime() {
        Instant sameTime = Instant.parse("2025-01-02T01:00:00Z");

        assertThrows(InvalidAuctionWindowException.class,
            () -> new AuctionWindow(sameTime, sameTime));
    }

    @Test
    @DisplayName("Should return true for timestamp within the auction window")
    void shouldReturnTrueWhenTimestampWithinWindow() {
        AuctionWindow auctionWindow = new AuctionWindow(
            Instant.parse("2025-01-01T01:00:00Z"),
            Instant.parse("2025-01-03T01:00:00Z")
        );

        assertThat(auctionWindow.isWithinWindow(Instant.parse("2025-01-02T01:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("Should return true for timestamp same as start time")
    void shouldReturnTrueWhenTimestampIsStartTime() {
        AuctionWindow auctionWindow = new AuctionWindow(
            Instant.parse("2025-01-01T01:00:00Z"),
            Instant.parse("2025-01-03T01:00:00Z")
        );

        assertThat(auctionWindow.isWithinWindow(Instant.parse("2025-01-01T01:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("Should return true for timestamp same as end time")
    void shouldReturnTrueWhenTimestampIsEndTime() {
        AuctionWindow auctionWindow = new AuctionWindow(
            Instant.parse("2025-01-01T01:00:00Z"),
            Instant.parse("2025-01-03T01:00:00Z")
        );

        assertThat(auctionWindow.isWithinWindow(Instant.parse("2025-01-03T01:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("Should return false for timestamp outside window")
    void shouldReturnFalseWhenTimestampIsOutsideOfWindow() {
        AuctionWindow auctionWindow = new AuctionWindow(
            Instant.parse("2025-01-02T01:00:00Z"),
            Instant.parse("2025-01-03T01:00:00Z")
        );

        assertThat(auctionWindow.isWithinWindow(Instant.parse("2025-01-04T00:00:00Z"))).isFalse();
        assertThat(auctionWindow.isWithinWindow(Instant.parse("2025-01-01T00:00:00Z"))).isFalse();
    }
}