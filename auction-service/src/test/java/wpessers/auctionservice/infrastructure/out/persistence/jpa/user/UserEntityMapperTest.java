package wpessers.auctionservice.infrastructure.out.persistence.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.domain.User;

class UserEntityMapperTest {

    private final UserEntityMapper mapper = new UserEntityMapper();

    @Test
    @DisplayName("Should map domain object to jpa entity")
    void shouldMapDomainToEntity() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email");

        UserEntity entity = mapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(userId);
        assertThat(entity.getUsername()).isEqualTo("username");
        assertThat(entity.getPassword()).isEqualTo("password");
        assertThat(entity.getEmail()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should map jpa entity to domain object")
    void shouldMapEntityToDomain() {
        UUID userId = UUID.randomUUID();
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setUsername("username");
        entity.setPassword("password");
        entity.setEmail("email");

        User user = mapper.toDomain(entity);

        assertThat(user.id()).isEqualTo(userId);
        assertThat(user.username()).isEqualTo("username");
        assertThat(user.password()).isEqualTo("password");
        assertThat(user.email()).isEqualTo("email");
    }
}