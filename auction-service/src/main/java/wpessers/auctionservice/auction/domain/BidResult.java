package wpessers.auctionservice.auction.domain;

import java.util.UUID;
import wpessers.auctionservice.shared.domain.Money;

public sealed interface BidResult {

    record Succes(Money previousAmount, Money newAmount, UUID previousBidder) implements BidResult {

    }

    record AuctionNotActive() implements BidResult {

    }

    record BidTooLow() implements BidResult {

    }
}
