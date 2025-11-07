package wpessers.auctionservice.infrastructure.out.time.system;

import java.time.Instant;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.application.port.out.TimeProvider;

@Component
public class SystemTimeProviderAdapter implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
