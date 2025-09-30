package wpessers.auctionservice.domain;

public record Auction(
        String name,
        String category,
        String description,
        String condition,
        Integer age
) {}
