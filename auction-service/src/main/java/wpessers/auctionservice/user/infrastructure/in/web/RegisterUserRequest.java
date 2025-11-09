package wpessers.auctionservice.user.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password,

    @Email(message = "Email must be valid")
    String email
) {

}
