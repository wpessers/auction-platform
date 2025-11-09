package wpessers.auctionservice.user.domain.exception;

public class InvalidUsernameException extends RuntimeException {

    public InvalidUsernameException(String message) {
        super(message);
    }
}
