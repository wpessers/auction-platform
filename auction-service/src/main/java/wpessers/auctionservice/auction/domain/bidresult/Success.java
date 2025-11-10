package wpessers.auctionservice.auction.domain.bidresult;

import java.util.UUID;
import wpessers.auctionservice.shared.domain.Money;

public record Success(
    Money previousAmount,
    Money newAmount,
    UUID previousBidder
) implements BidResult {

}
