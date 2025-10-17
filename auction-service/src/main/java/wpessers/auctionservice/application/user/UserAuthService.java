package wpessers.auctionservice.application.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.out.IdGenerator;
import wpessers.auctionservice.application.port.out.TokenGenerator;
import wpessers.auctionservice.application.port.out.UserStorage;
import wpessers.auctionservice.domain.User;
import wpessers.auctionservice.domain.exception.InvalidUsernameException;
import java.util.UUID;

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

    public void register(String username, String password, String email) {
        if (userStorage.exists(username)) {
            throw new InvalidUsernameException("Username already exists: " + username);
        }

        UUID id = idGenerator.generateId();
        User user = new User(id, username, passwordEncoder.encode(password), email);
        userStorage.save(user);
    }

    public String login(String username, String password) {
        User user = userStorage.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(user.password(), password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return tokenGenerator.generateToken(user.id());
    }
}
