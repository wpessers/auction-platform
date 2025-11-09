package wpessers.auctionservice.user.domain;

import java.util.UUID;

public record User(
    UUID id,
    String username,
    String password,
    String email
) {

}
