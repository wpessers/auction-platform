package wpessers.auctionservice.user.application.port.out;

import java.util.UUID;

public interface TokenGenerator {
    String generateAccessToken(UUID userId, String username);
    String generateRefreshToken();
    long getAccessTokenExpirationMs();
    long getRefreshTokenExpirationMs();
}
