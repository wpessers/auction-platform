package wpessers.auctionservice.user.application.port.out;

import java.util.Optional;
import java.util.UUID;
import wpessers.auctionservice.user.domain.User;

public interface UserStorage {

    void save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    boolean usernameExists(String username);

    boolean emailExists(String email);
}
