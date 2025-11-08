package wpessers.auctionservice.infrastructure.out.persistence.jpa.user;

import java.util.Optional;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.UserStorage;
import wpessers.auctionservice.domain.User;

@Component
public class JpaUserStorageAdapter implements UserStorage {

    private final UserRepository userRepository;
    private final UserEntityMapper mapper;

    public JpaUserStorageAdapter(UserRepository userRepository, UserEntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(User user) {
        userRepository.save(mapper.toEntity(user));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
