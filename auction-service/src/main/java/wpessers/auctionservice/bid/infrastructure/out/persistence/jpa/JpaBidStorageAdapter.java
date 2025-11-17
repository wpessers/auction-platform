package wpessers.auctionservice.bid.infrastructure.out.persistence.jpa;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.bid.application.port.out.BidStorage;
import wpessers.auctionservice.bid.domain.Bid;

@Component
public class JpaBidStorageAdapter implements BidStorage {

    private final BidRepository bidRepository;
    private final BidEntityMapper mapper;

    public JpaBidStorageAdapter(BidRepository bidRepository, BidEntityMapper mapper) {
        this.bidRepository = bidRepository;
        this.mapper = mapper;
    }

    @Async
    @Override
    public void save(Bid bid) {
        bidRepository.save(mapper.toEntity(bid));
    }
}
