package wpessers.auctionservice.shared.application.port.out;

import java.util.UUID;

public record UserClaims(UUID userId, String username) {

}
