package wpessers.auctionservice.application.port.out;

import wpessers.auctionservice.domain.User;
import java.util.Optional;

public interface UserStorage {

    void save(User user);

    Optional<User> findByUsername(String username);

    boolean exists(String username);
}
