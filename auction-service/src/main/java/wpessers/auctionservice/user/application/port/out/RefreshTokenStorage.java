package wpessers.auctionservice.user.application.port.out;

import wpessers.auctionservice.user.domain.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenStorage {
    void save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllForUser(UUID userId);
    void deleteExpired();
}
