package wpessers.auctionservice.domain.exception;

public class InvalidEndTimeException extends RuntimeException {

    public InvalidEndTimeException(String message) {
        super(message);
    }
}
