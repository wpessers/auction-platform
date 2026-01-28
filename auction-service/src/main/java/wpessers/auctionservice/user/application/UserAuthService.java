package wpessers.auctionservice.user.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wpessers.auctionservice.shared.application.port.out.IdGenerator;
import wpessers.auctionservice.user.application.port.in.RegisterUserCommand;
import wpessers.auctionservice.user.application.port.in.UserProfileResponse;
import wpessers.auctionservice.user.application.port.out.RefreshTokenStorage;
import wpessers.auctionservice.user.application.port.out.TokenGenerator;
import wpessers.auctionservice.user.application.port.out.UserStorage;
import wpessers.auctionservice.user.domain.RefreshToken;
import wpessers.auctionservice.user.domain.User;
import wpessers.auctionservice.user.domain.exception.InvalidEmailException;
import wpessers.auctionservice.user.domain.exception.InvalidRefreshTokenException;
import wpessers.auctionservice.user.domain.exception.InvalidUsernameException;
import wpessers.auctionservice.user.domain.exception.UserNotFoundException;

@Service
public class UserAuthService {

    private final IdGenerator idGenerator;
    private final UserStorage userStorage;
    private final TokenGenerator tokenGenerator;
    private final RefreshTokenStorage refreshTokenStorage;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(
        IdGenerator idGenerator,
        UserStorage userStorage,
        TokenGenerator tokenGenerator,
        RefreshTokenStorage refreshTokenStorage,
        PasswordEncoder passwordEncoder
    ) {
        this.idGenerator = idGenerator;
        this.userStorage = userStorage;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenStorage = refreshTokenStorage;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile(UUID userId) {
        User user = userStorage.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return new UserProfileResponse(user.id(), user.username(), user.email());
    }

    @Transactional
    public AuthTokens register(RegisterUserCommand command) {
        if (userStorage.usernameExists(command.username())) {
            throw new InvalidUsernameException("Username already exists: " + command.username());
        }
        if (userStorage.emailExists(command.email())) {
            throw new InvalidEmailException("Email already exists: " + command.email());
        }

        UUID id = idGenerator.generateId();
        User user = new User(
            id,
            command.username(),
            passwordEncoder.encode(command.password()),
            command.email()
        );
        userStorage.save(user);

        return generateAuthTokens(user);
    }

    @Transactional
    public AuthTokens login(String username, String password) {
        User user = userStorage.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.password())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return generateAuthTokens(user);
    }

    @Transactional
    public AuthTokens refreshTokens(String refreshTokenValue) {
        String tokenHash = hashToken(refreshTokenValue);
        RefreshToken storedToken = refreshTokenStorage.findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (!storedToken.isValid()) {
            throw new InvalidRefreshTokenException("Refresh token is expired or revoked");
        }

        // Revoke the old refresh token (token rotation for security)
        refreshTokenStorage.revokeAllForUser(storedToken.userId());

        User user = userStorage.findById(storedToken.userId())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        return generateAuthTokens(user);
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenStorage.revokeAllForUser(userId);
    }

    private AuthTokens generateAuthTokens(User user) {
        String accessToken = tokenGenerator.generateAccessToken(user.id(), user.username());
        String refreshTokenValue = tokenGenerator.generateRefreshToken();
        String tokenHash = hashToken(refreshTokenValue);

        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(tokenGenerator.getRefreshTokenExpirationMs());

        RefreshToken refreshToken = new RefreshToken(
            idGenerator.generateId(),
            user.id(),
            tokenHash,
            expiresAt,
            now,
            false
        );
        refreshTokenStorage.save(refreshToken);

        return new AuthTokens(
            accessToken,
            refreshTokenValue,
            tokenGenerator.getAccessTokenExpirationMs()
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
