package wpessers.auctionservice.infrastructure.out.time.fixed;

import wpessers.auctionservice.application.port.out.TimeProvider;
import java.time.Instant;

public class StubTimeProviderAdapter implements TimeProvider {

    private Instant fixedTime;

    @Override
    public Instant now() {
        return fixedTime;
    }

    public void setFixedTime(Instant instant) {
        fixedTime = instant;
    }
}
