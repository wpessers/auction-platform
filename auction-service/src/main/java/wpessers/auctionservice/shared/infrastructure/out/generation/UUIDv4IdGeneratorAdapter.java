package wpessers.auctionservice.shared.infrastructure.out.generation;

import java.util.UUID;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.shared.application.port.out.IdGenerator;

@Component
public class UUIDv4IdGeneratorAdapter implements IdGenerator {

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
