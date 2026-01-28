package wpessers.auctionservice.user.infrastructure.out.generation;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import wpessers.auctionservice.user.application.port.out.TokenGenerator;

public class StubTokenGeneratorAdapter implements TokenGenerator {

    private final Queue<String> accessTokens;
    private final Queue<String> refreshTokens;
    private long accessTokenExpirationMs = 900000;
    private long refreshTokenExpirationMs = 604800000;

    public StubTokenGeneratorAdapter() {
        this.accessTokens = new ArrayDeque<>();
        this.refreshTokens = new ArrayDeque<>();
    }

    @Override
    public String generateAccessToken(UUID userId, String username) {
        return accessTokens.remove();
    }

    @Override
    public String generateRefreshToken() {
        return refreshTokens.remove();
    }

    @Override
    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    @Override
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public void addAccessToken(String token) {
        accessTokens.add(token);
    }

    public void addRefreshToken(String token) {
        refreshTokens.add(token);
    }

    public void addToken(String accessToken) {
        accessTokens.add(accessToken);
        refreshTokens.add("refresh-" + accessToken);
    }
}
