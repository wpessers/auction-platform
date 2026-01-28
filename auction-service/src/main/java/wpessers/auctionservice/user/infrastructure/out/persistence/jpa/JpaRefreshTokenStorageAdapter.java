package wpessers.auctionservice.user.infrastructure.out.persistence.jpa;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wpessers.auctionservice.user.application.port.out.RefreshTokenStorage;
import wpessers.auctionservice.user.domain.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaRefreshTokenStorageAdapter implements RefreshTokenStorage {
    private final RefreshTokenRepository repository;

    public JpaRefreshTokenStorageAdapter(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void save(RefreshToken token) {
        RefreshTokenEntity entity = new RefreshTokenEntity(
            token.id(),
            token.userId(),
            token.tokenHash(),
            token.expiresAt(),
            token.createdAt(),
            token.revoked()
        );
        repository.save(entity);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash)
            .map(this::toDomain);
    }

    @Override
    @Transactional
    public void revokeAllForUser(UUID userId) {
        repository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpired() {
        repository.deleteByExpiresAtBefore(Instant.now());
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
            entity.getId(),
            entity.getUserId(),
            entity.getTokenHash(),
            entity.getExpiresAt(),
            entity.getCreatedAt(),
            entity.isRevoked()
        );
    }
}
