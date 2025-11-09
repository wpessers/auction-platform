package wpessers.auctionservice.auction.domain.bidresult;

public sealed interface BidResult permits Success, AuctionNotActive, BidAmountTooLow {

}
