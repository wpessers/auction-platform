package wpessers.auctionservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;

import java.time.Instant;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuctionTests {

    @Test
    @DisplayName("Should throw exception when starting price is negative")
    void shouldThrowOnInvalidStartingPrice() {
        // Given
        UUID id = randomUUID();
        String name = "Mona Lisa";
        String description = "16th Century painting by Leonardo da Vinci";
        Instant startTime = Instant.parse("2025-01-02T01:00:00Z");
        Instant endTime = Instant.parse("2025-01-02T02:00:00Z");
        AuctionWindow auctionWindow = new AuctionWindow(startTime, endTime);
        Money startingPrice = new Money(-10);

        // When / Then
        assertThrows(InvalidStartingPriceException.class,
            () -> Auction.create(id, name, description, auctionWindow, startingPrice));
    }
}