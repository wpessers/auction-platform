package wpessers.auctionservice.shared.application.port.out;

import java.security.Principal;
import java.util.UUID;

public record UserClaims(UUID userId, String username) implements Principal {

    @Override
    public String getName() {
        return userId.toString();
    }
}
