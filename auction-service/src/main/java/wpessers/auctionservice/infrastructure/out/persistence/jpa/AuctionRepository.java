package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import wpessers.auctionservice.domain.AuctionStatus;

public interface AuctionRepository extends JpaRepository<AuctionEntity, UUID> {

    Stream<AuctionEntity> getAuctionEntitiesByStatusIs(AuctionStatus status);
}
