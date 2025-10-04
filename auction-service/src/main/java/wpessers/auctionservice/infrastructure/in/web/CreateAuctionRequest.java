package wpessers.auctionservice.infrastructure.in.web;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;

public record CreateAuctionRequest(
    @NotBlank(message = "Auction name is required")
    String name,

    @NotNull(message = "Auction description is required")
    String description,

    @NotNull(message = "Auction end time is required")
    @Future(message = "Auction end time must be in the future")
    Instant endTime,

    @NotNull(message = "Starting price is required")
    @PositiveOrZero(message = "Starting price can not be negative")
    Double startingPrice
) {

}
