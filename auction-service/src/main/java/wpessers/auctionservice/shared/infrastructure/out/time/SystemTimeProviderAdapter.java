package wpessers.auctionservice.shared.infrastructure.out.time;

import java.time.Instant;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;

@Component
public class SystemTimeProviderAdapter implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
