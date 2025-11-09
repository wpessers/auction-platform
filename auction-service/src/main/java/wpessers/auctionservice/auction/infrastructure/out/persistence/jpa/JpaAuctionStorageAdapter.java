package wpessers.auctionservice.auction.infrastructure.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;

@Component
public class JpaAuctionStorageAdapter implements AuctionStorage {

    private final AuctionRepository auctionRepository;
    private final AuctionEntityMapper mapper;

    public JpaAuctionStorageAdapter(
        AuctionRepository auctionRepository,
        AuctionEntityMapper mapper
    ) {
        this.auctionRepository = auctionRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Auction auction) {
        auctionRepository.save(mapper.toEntity(auction));
    }

    @Override
    public Optional<Auction> findById(UUID auctionId) {
        return auctionRepository.findById(auctionId).map(mapper::toDomain);
    }

    @Override
    public Stream<Auction> getActiveAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.ACTIVE)
            .stream()
            .map(mapper::toDomain);
    }
}
