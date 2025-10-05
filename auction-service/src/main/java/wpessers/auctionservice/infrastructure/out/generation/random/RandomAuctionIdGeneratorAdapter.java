package wpessers.auctionservice.infrastructure.out.generation.random;

import java.util.UUID;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.AuctionIdGenerator;

@Component
public class RandomAuctionIdGeneratorAdapter implements AuctionIdGenerator {

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
