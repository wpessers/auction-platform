package wpessers.auctionservice.bid.infrastructure.out.persistence.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<BidEntity, Long> {

    List<BidEntity> findByAuctionIdOrderByTimestampDesc(UUID auctionId);
}
