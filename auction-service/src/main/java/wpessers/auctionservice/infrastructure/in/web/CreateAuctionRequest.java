package wpessers.auctionservice.infrastructure.in.web;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateAuctionRequest(
    @NotBlank(message = "Auction name is required")
    String name,

    @NotNull(message = "Auction description is required")
    String description,

    @FutureOrPresent(message = "Auction start time cannot be in the past")
    Instant startTime,

    @NotNull(message = "Auction end time is required")
    @Future(message = "Auction end time must be in the future")
    Instant endTime,

    @NotNull(message = "Starting price is required")
    @PositiveOrZero(message = "Starting price cannot be negative")
    BigDecimal startingPrice
) {

}
