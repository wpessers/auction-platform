package wpessers.auctionservice.domain.exception;

public class InvalidStartingPriceException extends RuntimeException {

    public InvalidStartingPriceException(String message) {
        super(message);
    }
}
