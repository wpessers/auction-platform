package wpessers.auctionservice.auction.infrastructure.out.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import wpessers.auctionservice.auction.domain.AuctionStatus;

public interface AuctionRepository extends JpaRepository<AuctionEntity, UUID> {

    List<AuctionEntity> findByStatus(AuctionStatus status);
}
