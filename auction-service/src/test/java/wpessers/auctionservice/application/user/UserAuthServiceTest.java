package wpessers.auctionservice.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import wpessers.auctionservice.application.port.in.command.RegisterUserCommand;
import wpessers.auctionservice.domain.User;
import wpessers.auctionservice.domain.exception.InvalidEmailException;
import wpessers.auctionservice.domain.exception.InvalidUsernameException;
import wpessers.auctionservice.infrastructure.out.generation.fixed.StubIdGeneratorAdapter;
import wpessers.auctionservice.infrastructure.out.generation.fixed.StubTokenGeneratorAdapter;
import wpessers.auctionservice.infrastructure.out.persistence.inmemory.FakeUserStorageAdapter;

class UserAuthServiceTest {

    private StubIdGeneratorAdapter idGenerator;
    private FakeUserStorageAdapter userStorage;
    private StubTokenGeneratorAdapter tokenProvider;
    private PasswordEncoder passwordEncoder;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        this.idGenerator = new StubIdGeneratorAdapter();
        this.userStorage = new FakeUserStorageAdapter();
        this.tokenProvider = new StubTokenGeneratorAdapter();
        this.passwordEncoder = NoOpPasswordEncoder.getInstance();
        this.userAuthService = new UserAuthService(
            idGenerator,
            userStorage,
            tokenProvider,
            passwordEncoder
        );
    }

    @Test
    @DisplayName("Should register a new user")
    void shouldRegister() {
        idGenerator.addId(UUID.randomUUID());
        RegisterUserCommand registerUserCommand = new RegisterUserCommand(
            "username",
            "password",
            "test.user@email.com"
        );

        userAuthService.register(registerUserCommand);

        assertThat(userStorage.findByUsername("username")).isPresent();
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
    @DisplayName("Should login a user and return token")
    void shouldLogin() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "test@email.com");
        userStorage.save(user);
        tokenProvider.addToken("token-" + userId);

        String token = userAuthService.login("username", "password");

        assertThat(token).isEqualTo("token-" + userId);
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
}