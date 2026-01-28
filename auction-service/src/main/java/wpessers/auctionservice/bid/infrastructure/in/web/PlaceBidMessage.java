package wpessers.auctionservice.bid.infrastructure.in.web;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record PlaceBidMessage(
    @NotNull UUID auctionId,
    @NotNull @Positive @DecimalMax(value = "999999999.99", message = "Bid amount cannot exceed $999,999,999.99") BigDecimal amount
) {

}
