package wpessers.auctionservice.infrastructure.out.persistence.jpa;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.AuctionStorage;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import java.util.List;

@Component
public class JpaAuctionStorageAdapter implements AuctionStorage {

    private final AuctionEntityMapper mapper;
    private final AuctionRepository auctionRepository;

    public JpaAuctionStorageAdapter(
        AuctionEntityMapper mapper,
        AuctionRepository auctionRepository
    ) {
        this.mapper = mapper;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public void save(Auction auction) {
        AuctionEntity entity = mapper.toEntity(auction);
        auctionRepository.save(entity);
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return auctionRepository.getAuctionEntitiesByStatusIs(AuctionStatus.ACTIVE).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
