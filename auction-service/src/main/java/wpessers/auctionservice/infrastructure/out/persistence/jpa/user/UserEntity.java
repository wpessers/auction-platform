package wpessers.auctionservice.infrastructure.out.persistence.jpa.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table
public class UserEntity {

    @Id
    private UUID id;



}
