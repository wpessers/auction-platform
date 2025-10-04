package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<AuctionEntity, UUID> {

}
