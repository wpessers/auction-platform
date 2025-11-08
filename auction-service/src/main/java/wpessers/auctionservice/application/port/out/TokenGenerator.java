package wpessers.auctionservice.application.port.out;

public interface TokenGenerator {

    String generateToken(String username);
}
