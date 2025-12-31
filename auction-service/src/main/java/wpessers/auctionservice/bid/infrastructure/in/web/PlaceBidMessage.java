package wpessers.auctionservice.bid.infrastructure.in.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record PlaceBidMessage(
    @NotNull UUID auctionId,
    @NotNull @Positive BigDecimal amount
) {

}
