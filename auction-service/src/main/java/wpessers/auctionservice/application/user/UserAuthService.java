package wpessers.auctionservice.application.user;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.in.command.RegisterUserCommand;
import wpessers.auctionservice.application.port.out.IdGenerator;
import wpessers.auctionservice.application.port.out.TokenGenerator;
import wpessers.auctionservice.application.port.out.UserStorage;
import wpessers.auctionservice.domain.User;
import wpessers.auctionservice.domain.exception.InvalidEmailException;
import wpessers.auctionservice.domain.exception.InvalidUsernameException;

@Service
public class UserAuthService {

    private final IdGenerator idGenerator;
    private final UserStorage userStorage;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(
        IdGenerator idGenerator,
        UserStorage userStorage,
        TokenGenerator tokenGenerator,
        PasswordEncoder passwordEncoder
    ) {
        this.idGenerator = idGenerator;
        this.userStorage = userStorage;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterUserCommand command) {
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
    }

    public String login(String username, String password) {
        User user = userStorage.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(user.password(), password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return tokenGenerator.generateToken(user.username());
    }
}
