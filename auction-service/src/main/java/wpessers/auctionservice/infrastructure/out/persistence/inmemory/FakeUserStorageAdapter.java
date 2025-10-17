package wpessers.auctionservice.infrastructure.out.persistence.inmemory;

import ch.qos.logback.classic.util.LogbackMDCAdapterSimple;
import wpessers.auctionservice.application.port.out.UserStorage;
import wpessers.auctionservice.domain.User;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

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
    public boolean exists(String username) {
        return users.containsKey(username);
    }
}
