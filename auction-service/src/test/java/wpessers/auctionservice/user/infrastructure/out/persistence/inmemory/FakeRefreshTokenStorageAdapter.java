package wpessers.auctionservice.user.infrastructure.out.persistence.inmemory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import wpessers.auctionservice.user.application.port.out.RefreshTokenStorage;
import wpessers.auctionservice.user.domain.RefreshToken;

public class FakeRefreshTokenStorageAdapter implements RefreshTokenStorage {

    private final Map<String, RefreshToken> tokensByHash = new HashMap<>();

    @Override
    public void save(RefreshToken token) {
        tokensByHash.put(token.tokenHash(), token);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return Optional.ofNullable(tokensByHash.get(tokenHash));
    }

    @Override
    public void revokeAllForUser(UUID userId) {
        tokensByHash.replaceAll((hash, token) -> {
            if (token.userId().equals(userId) && !token.revoked()) {
                return token.revoke();
            }
            return token;
        });
    }

    @Override
    public void deleteExpired() {
        Instant now = Instant.now();
        tokensByHash.entrySet().removeIf(e -> e.getValue().expiresAt().isBefore(now));
    }
}
