package wpessers.auctionservice.infrastructure.out.persistence.inmemory;

import java.util.HashMap;
import java.util.Optional;
import wpessers.auctionservice.application.port.out.UserStorage;
import wpessers.auctionservice.domain.User;

public class FakeUserStorageAdapter implements UserStorage {

    private final HashMap<String, User> users;

    public FakeUserStorageAdapter() {
        this.users = new HashMap<>();
    }

    @Override
    public void save(User user) {
        users.put(user.username(), user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    @Override
    public boolean emailExists(String email) {
        return users.values().stream().anyMatch((user) -> user.email().equals(email));
    }
}
