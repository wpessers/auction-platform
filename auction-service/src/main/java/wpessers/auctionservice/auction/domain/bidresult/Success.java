package wpessers.auctionservice.auction.domain.bidresult;

import wpessers.auctionservice.shared.domain.Money;
import java.util.UUID;

public record Success(
    Money previousAmount,
    Money newAmount,
    UUID previousBidder
) implements BidResult {

}
