package wpessers.auctionservice.infrastructure.out.persistence.jpa.auction;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.AuctionStorage;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;

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
    public Optional<Auction> findById(UUID auctionId) {
        return auctionRepository.findById(auctionId).map(mapper::toDomain);
    }

    @Override
    public Stream<Auction> getActiveAuctions() {
        return auctionRepository.getAuctionEntitiesByStatusIs(AuctionStatus.ACTIVE)
            .stream()
            .map(mapper::toDomain);
    }
}
