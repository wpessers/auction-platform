package wpessers.auctionservice.user.application.port.out;

public interface TokenGenerator {

    String generateToken(String username);
}
