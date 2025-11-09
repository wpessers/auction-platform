package wpessers.auctionservice.auction.domain.exception;

public class AuctionNotFoundException extends RuntimeException {

    public AuctionNotFoundException(String message) {
        super(message);
    }
}
