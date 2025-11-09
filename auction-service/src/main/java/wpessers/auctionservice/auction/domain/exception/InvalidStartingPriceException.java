package wpessers.auctionservice.auction.domain.exception;

public class InvalidStartingPriceException extends RuntimeException {

    public InvalidStartingPriceException(String message) {
        super(message);
    }
}
