package wpessers.auctionservice.infrastructure.out.persistence;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.Auction;

@Service
public class AuctionJpaAdapter implements AuctionStoragePort {

    private final AuctionRepository auctionRepository;

    public AuctionJpaAdapter(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Override
    public void save(Auction auction) {
        // TODO: Refactor
        AuctionEntity auctionEntity = new AuctionEntity();
        auctionEntity.setId(auction.getId());
        auctionEntity.setName(auction.getName());
        auctionEntity.setDescription(auction.getDescription());
        auctionEntity.setStartTime(auction.getAuctionWindow().startTime());
        auctionEntity.setEndTime(auction.getAuctionWindow().endTime());
        auctionEntity.setStartingPrice(auction.getStartingPrice().amount());
        auctionEntity.setStatus(auction.getStatus());

        auctionRepository.save(auctionEntity);
    }
}
