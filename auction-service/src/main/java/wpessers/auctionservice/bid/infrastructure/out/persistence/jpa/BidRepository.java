package wpessers.auctionservice.bid.infrastructure.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<BidEntity, Long> {

}
