package wpessers.auctionservice.application.port.in.command;

public record RegisterUserCommand(
    String username,
    String password,
    String email
) {

}
