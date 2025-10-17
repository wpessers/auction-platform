package wpessers.auctionservice.application.port.out;

import java.util.UUID;

public interface TokenGenerator {

    String generateToken(UUID userId);
}
