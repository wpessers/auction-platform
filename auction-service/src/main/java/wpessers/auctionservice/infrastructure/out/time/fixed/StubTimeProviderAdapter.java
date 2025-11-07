package wpessers.auctionservice.infrastructure.out.time.fixed;

import java.time.Instant;
import wpessers.auctionservice.application.port.out.TimeProvider;

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
