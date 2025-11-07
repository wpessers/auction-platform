package wpessers.auctionservice.infrastructure.out.generation.random;

import java.util.UUID;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.IdGenerator;

@Component
public class RandomIdGeneratorAdapter implements IdGenerator {

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
