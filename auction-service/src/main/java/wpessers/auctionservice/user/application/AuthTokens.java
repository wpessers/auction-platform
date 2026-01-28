package wpessers.auctionservice.user.application;

public record AuthTokens(
    String accessToken,
    String refreshToken,
    long expiresIn
) { }
