package wpessers.auctionservice.user.infrastructure.in.web;

public record AuthTokensResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) { }
