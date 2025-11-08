package wpessers.auctionservice.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {

}
