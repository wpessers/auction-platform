package wpessers.auctionservice.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;

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

        // When / Then
        assertThrows(InvalidStartingPriceException.class,
            () -> Auction.create(id, name, description, auctionWindow, startingPrice));
    }
}