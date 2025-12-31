package wpessers.auctionservice.shared.application.port.out;

public interface TokenParser {

    UserClaims parseToken(String token);
}
