package wpessers.auctionservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;
import wpessers.auctionservice.domain.exception.InvalidStateTransitionException;
import wpessers.auctionservice.fixtures.AuctionBuilder;

class AuctionTests {

    @Test
    @DisplayName("Should throw exception when starting price is negative")
    void shouldThrowOnInvalidStartingPrice() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Charizard Holo";
        String description = "Holographic Charizard card";
        Instant startTime = Instant.parse("2025-01-02T01:00:00Z");
        Instant endTime = Instant.parse("2025-01-02T02:00:00Z");
        AuctionWindow auctionWindow = new AuctionWindow(startTime, endTime);
        Money startingPrice = new Money(-10);

        // When & Then
        assertThrows(InvalidStartingPriceException.class,
            () -> new Auction(id, name, description, auctionWindow, startingPrice,
                AuctionStatus.OPEN));
    }

    @Test
    @DisplayName("Should transition status to OPEN when starting a SCHEDULED auction")
    void shouldOpenAuction() {
        // Given
        Auction auction = new AuctionBuilder().withStatus(AuctionStatus.SCHEDULED).build();

        // When
        auction.start();

        // Then
        assertThat(auction.isOpen()).isTrue();
    }


    @ParameterizedTest
    @EnumSource(names = {"OPEN", "CLOSED"})
    @DisplayName("Should throw exception when starting a non-SCHEDULED auction")
    void shouldThrowWhenStartingNonScheduledAuction(AuctionStatus status) {
        // Given
        Auction auction = new AuctionBuilder().withStatus(status).build();

        // When & Then
        assertThrows(InvalidStateTransitionException.class, auction::start);
    }

    @ParameterizedTest
    @EnumSource(names = {"OPEN", "SCHEDULED"})
    @DisplayName("Should transition status to CLOSED when ending an auction")
    void shouldCloseAuction(AuctionStatus status) {
        // Given
        Auction auction = new AuctionBuilder().withStatus(status).build();

        // When
        auction.end();

        // Then
        assertThat(auction.isClosed()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when ending a CLOSED auction")
    void shouldThrowWhenEndingClosedAuction() {
        // Given
        Auction auction = new AuctionBuilder().withStatus(AuctionStatus.CLOSED).build();

        // When & Then
        assertThrows(InvalidStateTransitionException.class, auction::end);
    }
}