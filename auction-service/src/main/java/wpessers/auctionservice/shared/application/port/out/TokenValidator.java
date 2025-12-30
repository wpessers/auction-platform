package wpessers.auctionservice.shared.application.port.out;

public interface TokenValidator {

    UserClaims parseToken(String token);
}
