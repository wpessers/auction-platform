package wpessers.auctionservice.infrastructure.out.time.system;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.out.TimeProvider;
import java.time.Instant;

@Service
public class SystemTimeProviderAdapter implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
