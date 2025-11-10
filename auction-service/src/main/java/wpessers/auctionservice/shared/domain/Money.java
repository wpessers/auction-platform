package wpessers.auctionservice.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {

    public Money {
        amount = Objects.requireNonNull(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public Money(double amount) {
        this(BigDecimal.valueOf(amount));
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isLessThanOrEqualTo(Money other) {
        return this.amount.compareTo(other.amount) <= 0;
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }
}
