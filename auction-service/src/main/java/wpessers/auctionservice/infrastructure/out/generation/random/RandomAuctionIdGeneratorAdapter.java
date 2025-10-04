package wpessers.auctionservice.infrastructure.out.generation.random;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.out.AuctionIdGenerator;
import java.util.UUID;

@Service
public class RandomAuctionIdGeneratorAdapter implements AuctionIdGenerator {

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
