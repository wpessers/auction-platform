package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import wpessers.auctionservice.domain.AuctionStatus;
import java.util.List;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<AuctionEntity, UUID> {

    List<AuctionEntity> getAuctionEntitiesByStatusIs(AuctionStatus status);
}
