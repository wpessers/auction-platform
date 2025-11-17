package wpessers.auctionservice.shared.infrastructure.out.time;

import wpessers.auctionservice.shared.application.port.out.TimeProvider;
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
