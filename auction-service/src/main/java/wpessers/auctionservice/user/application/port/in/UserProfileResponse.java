package wpessers.auctionservice.user.application.port.in;

import java.util.UUID;

public record UserProfileResponse(
    UUID userId,
    String username,
    String email
) {

}
