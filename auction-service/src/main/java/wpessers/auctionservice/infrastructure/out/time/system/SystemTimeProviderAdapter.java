package wpessers.auctionservice.infrastructure.out.time.system;

import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.TimeProvider;
import java.time.Instant;

@Component
public class SystemTimeProviderAdapter implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
