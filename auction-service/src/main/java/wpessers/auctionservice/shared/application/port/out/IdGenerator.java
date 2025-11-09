package wpessers.auctionservice.shared.application.port.out;

import java.util.UUID;

public interface IdGenerator {

    UUID generateId();
}
