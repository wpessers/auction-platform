package wpessers.auctionservice.shared.infrastructure.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.shared.application.port.out.TokenParser;
import wpessers.auctionservice.shared.application.port.out.UserClaims;
import wpessers.auctionservice.user.application.port.out.TokenGenerator;

@Component
public class JwtTokenProviderAdapter implements TokenGenerator, TokenParser {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final SecureRandom secureRandom;

    public JwtTokenProviderAdapter(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
        @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs
    ) {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateAccessToken(UUID userId, String username) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("username", username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
            .signWith(key)
            .compact();
    }

    @Override
    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    @Override
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    @Override
    public UserClaims parseToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return new UserClaims(
            UUID.fromString(claims.getSubject()),
            claims.get("username", String.class)
        );
    }
}
