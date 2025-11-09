package wpessers.auctionservice.user.infrastructure.out.generation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.user.application.port.out.TokenGenerator;

@Component
public class JwtTokenGeneratorAdapter implements TokenGenerator {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenGeneratorAdapter(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(String username) {
        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
    }
}
