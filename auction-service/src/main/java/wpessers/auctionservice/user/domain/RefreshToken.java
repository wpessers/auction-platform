package wpessers.auctionservice.user.domain;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
    UUID id,
    UUID userId,
    String tokenHash,
    Instant expiresAt,
    Instant createdAt,
    boolean revoked
) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, createdAt, true);
    }
}
