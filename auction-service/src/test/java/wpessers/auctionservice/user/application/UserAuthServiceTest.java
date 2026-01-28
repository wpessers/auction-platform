package wpessers.auctionservice.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import wpessers.auctionservice.shared.infrastructure.out.generation.StubIdGeneratorAdapter;
import wpessers.auctionservice.user.application.port.in.RegisterUserCommand;
import wpessers.auctionservice.user.domain.User;
import wpessers.auctionservice.user.application.port.in.UserProfileResponse;
import wpessers.auctionservice.user.domain.exception.InvalidEmailException;
import wpessers.auctionservice.user.domain.exception.InvalidUsernameException;
import wpessers.auctionservice.user.domain.exception.UserNotFoundException;
import wpessers.auctionservice.user.infrastructure.out.generation.StubTokenGeneratorAdapter;
import wpessers.auctionservice.user.infrastructure.out.persistence.inmemory.FakeRefreshTokenStorageAdapter;
import wpessers.auctionservice.user.infrastructure.out.persistence.inmemory.FakeUserStorageAdapter;

class UserAuthServiceTest {

    private StubIdGeneratorAdapter idGenerator;
    private FakeUserStorageAdapter userStorage;
    private StubTokenGeneratorAdapter tokenGenerator;
    private FakeRefreshTokenStorageAdapter refreshTokenStorage;
    private PasswordEncoder passwordEncoder;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        this.idGenerator = new StubIdGeneratorAdapter();
        this.userStorage = new FakeUserStorageAdapter();
        this.tokenGenerator = new StubTokenGeneratorAdapter();
        this.refreshTokenStorage = new FakeRefreshTokenStorageAdapter();
        this.passwordEncoder = NoOpPasswordEncoder.getInstance();
        this.userAuthService = new UserAuthService(
            idGenerator,
            userStorage,
            tokenGenerator,
            refreshTokenStorage,
            passwordEncoder
        );
    }

    @Test
    @DisplayName("Should register a new user and return tokens")
    void shouldRegister() {
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        idGenerator.addId(userId);
        idGenerator.addId(tokenId);
        tokenGenerator.addAccessToken("access-token-" + userId);
        tokenGenerator.addRefreshToken("refresh-token-" + userId);
        RegisterUserCommand registerUserCommand = new RegisterUserCommand(
            "username",
            "password",
            "test.user@email.com"
        );

        AuthTokens tokens = userAuthService.register(registerUserCommand);

        assertThat(userStorage.findByUsername("username")).isPresent();
        assertThat(tokens.accessToken()).isEqualTo("access-token-" + userId);
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token-" + userId);
    }

    @Test
    @DisplayName("Should throw exception when registering with a username that is already used")
    void shouldThrowOnDuplicateUsername() {
        User existingUser = new User(UUID.randomUUID(), "username", "password", "email");
        userStorage.save(existingUser);
        idGenerator.addId(UUID.randomUUID());

        RegisterUserCommand registerUserCommand = new RegisterUserCommand(
            "username",
            "password",
            "email"
        );
        assertThrows(InvalidUsernameException.class,
            () -> userAuthService.register(registerUserCommand));
    }

    @Test
    @DisplayName("Should throw exception when registering with an email that is already used")
    void shouldThrowOnDuplicateEmail() {
        User existingUser = new User(UUID.randomUUID(), "username", "password", "email");
        userStorage.save(existingUser);
        idGenerator.addId(UUID.randomUUID());

        RegisterUserCommand registerUserCommand = new RegisterUserCommand(
            "other-username",
            "password",
            "email"
        );
        assertThrows(InvalidEmailException.class,
            () -> userAuthService.register(registerUserCommand));
    }

    @Test
    @DisplayName("Should login a user and return tokens")
    void shouldLogin() {
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "test@email.com");
        userStorage.save(user);
        idGenerator.addId(tokenId);
        tokenGenerator.addAccessToken("access-token-" + userId);
        tokenGenerator.addRefreshToken("refresh-token-" + userId);

        AuthTokens tokens = userAuthService.login("username", "password");

        assertThat(tokens.accessToken()).isEqualTo("access-token-" + userId);
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token-" + userId);
    }

    @Test
    @DisplayName("Should throw exception when login with invalid username")
    void shouldThrowOnInvalidUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userAuthService.login("nonexistent", "password"));
    }

    @Test
    @DisplayName("Should throw exception when login with invalid password")
    void shouldThrowOnInvalidPassword() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "test@email.com");
        userStorage.save(user);

        assertThrows(IllegalArgumentException.class,
            () -> userAuthService.login("username", "wrongpassword"));
    }

    @Test
    @DisplayName("Should return user profile")
    void shouldGetProfile() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "test@email.com");
        userStorage.save(user);

        UserProfileResponse profile = userAuthService.getProfile(userId);

        assertThat(profile.userId()).isEqualTo(userId);
        assertThat(profile.username()).isEqualTo("username");
        assertThat(profile.email()).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("Should throw exception when getting profile for nonexistent user")
    void shouldThrowOnProfileNotFound() {
        UUID nonexistentUserId = UUID.randomUUID();

        assertThrows(UserNotFoundException.class,
            () -> userAuthService.getProfile(nonexistentUserId));
    }
}