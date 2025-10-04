package wpessers.auctionservice.application.port.out;

import java.util.UUID;

public interface AuctionIdGenerator {

    UUID generateId();
}
