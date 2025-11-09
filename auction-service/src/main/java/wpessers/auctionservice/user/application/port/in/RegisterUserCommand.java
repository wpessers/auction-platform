package wpessers.auctionservice.user.application.port.in;

public record RegisterUserCommand(
    String username,
    String password,
    String email
) {

}
