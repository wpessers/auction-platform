package wpessers.auctionservice.application.port.out;

import java.time.Instant;

public interface TimeProvider {

    public Instant now();
}
