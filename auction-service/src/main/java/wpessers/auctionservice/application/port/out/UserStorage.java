package wpessers.auctionservice.application.port.out;

import java.util.Optional;
import wpessers.auctionservice.domain.User;

public interface UserStorage {

    void save(User user);

    Optional<User> findByUsername(String username);

    boolean usernameExists(String username);

    boolean emailExists(String email);
}
