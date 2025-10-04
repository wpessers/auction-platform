package wpessers.auctionservice.infrastructure.out.generation.random;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.AuctionIdGenerator;
import java.util.UUID;

@Component
public class RandomAuctionIdGeneratorAdapter implements AuctionIdGenerator {

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
