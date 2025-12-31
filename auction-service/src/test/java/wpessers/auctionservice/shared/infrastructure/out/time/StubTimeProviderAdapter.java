package wpessers.auctionservice.shared.infrastructure.out.time;

import java.time.Instant;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;

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
