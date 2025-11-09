package wpessers.auctionservice.user.infrastructure.out.persistence.jpa;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.user.domain.User;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setEmail(user.email());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getUsername(),
            entity.getPassword(),
            entity.getEmail()
        );
    }
}
