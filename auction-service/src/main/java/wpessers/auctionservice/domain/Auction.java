package wpessers.auctionservice.domain;

import wpessers.auctionservice.domain.exception.InvalidStartingPriceException;
import java.util.UUID;

public class Auction {

    private final UUID id;
    private final String name;
    private final String description;
    private final AuctionWindow auctionWindow;
    private final Money startingPrice;

    private Auction(
        UUID id, String name, String description,
        AuctionWindow auctionWindow, Money startingPrice
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.auctionWindow = auctionWindow;
        this.startingPrice = startingPrice;
    }

    public static Auction create(
        UUID id,
        String name,
        String description,
        AuctionWindow auctionWindow,
        Money startingPrice
    ) {
        if (startingPrice.isNegative()) {
            throw new InvalidStartingPriceException("Starting price cannot be negative");
        }
        return new Auction(id, name, description, auctionWindow, startingPrice);
    }
}
