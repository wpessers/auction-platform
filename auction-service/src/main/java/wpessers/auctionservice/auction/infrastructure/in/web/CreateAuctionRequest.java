package wpessers.auctionservice.auction.infrastructure.in.web;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateAuctionRequest(
    @NotBlank(message = "Auction name is required")
    @Size(max = 200, message = "Auction name cannot exceed 200 characters")
    String name,

    @NotNull(message = "Auction description is required")
    @Size(max = 5000, message = "Auction description cannot exceed 5000 characters")
    String description,

    @FutureOrPresent(message = "Auction start time cannot be in the past")
    Instant startTime,

    @NotNull(message = "Auction end time is required")
    @Future(message = "Auction end time must be in the future")
    Instant endTime,

    @NotNull(message = "Starting price is required")
    @PositiveOrZero(message = "Starting price cannot be negative")
    @DecimalMax(value = "999999999.99", message = "Starting price cannot exceed $999,999,999.99")
    BigDecimal startingPrice
) {
    @AssertTrue(message = "End time must be after start time")
    public boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) {
            return true; // Let @NotNull handle null checks
        }
        return endTime.isAfter(startTime);
    }
}
