package wpessers.auctionservice.auction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wpessers.auctionservice.auction.domain.bidresult.AuctionNotActive;
import wpessers.auctionservice.auction.domain.bidresult.BidAmountTooLow;
import wpessers.auctionservice.auction.domain.bidresult.BidResult;
import wpessers.auctionservice.auction.domain.bidresult.Success;
import wpessers.auctionservice.auction.domain.exception.InvalidStartingPriceException;
import wpessers.auctionservice.auction.domain.exception.InvalidStateTransitionException;
import wpessers.auctionservice.fixtures.AuctionBuilder;
import wpessers.auctionservice.shared.domain.Money;

class AuctionTests {

    @Test
    @DisplayName("Should throw exception when starting price is negative")
    void shouldThrowOnInvalidStartingPrice() {
        UUID id = UUID.randomUUID();
        String name = "Charizard Holo";
        String description = "Holographic Charizard card";
        Instant startTime = Instant.parse("2025-01-02T01:00:00Z");
        Instant endTime = Instant.parse("2025-01-02T02:00:00Z");
        AuctionWindow auctionWindow = new AuctionWindow(startTime, endTime);
        Money startingPrice = new Money(-10);

        assertThrows(InvalidStartingPriceException.class,
            () -> new Auction(id, name, description, auctionWindow, startingPrice,
                AuctionStatus.ACTIVE));
    }

    @Nested
    class AuctionStateTransitionTests {

        @Test
        @DisplayName("Should transition status to OPEN when starting a SCHEDULED auction")
        void shouldOpenAuction() {
            Auction auction = new AuctionBuilder().withStatus(AuctionStatus.SCHEDULED).build();

            auction.start();

            assertThat(auction.isActive()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(names = {"ACTIVE", "CLOSED"})
        @DisplayName("Should throw exception when starting a non-SCHEDULED auction")
        void shouldThrowWhenStartingNonScheduledAuction(AuctionStatus status) {
            Auction auction = new AuctionBuilder().withStatus(status).build();

            assertThrows(InvalidStateTransitionException.class, auction::start);
        }

        @ParameterizedTest
        @EnumSource(names = {"ACTIVE", "SCHEDULED"})
        @DisplayName("Should transition status to CLOSED when ending an auction")
        void shouldCloseAuction(AuctionStatus status) {
            Auction auction = new AuctionBuilder().withStatus(status).build();

            auction.end();

            assertThat(auction.isClosed()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when ending a CLOSED auction")
        void shouldThrowWhenEndingClosedAuction() {
            Auction auction = new AuctionBuilder().withStatus(AuctionStatus.CLOSED).build();

            assertThrows(InvalidStateTransitionException.class, auction::end);
        }
    }

    @Nested
    class AuctionPlaceBidTests {

        private static final Instant AUCTION_START = Instant.parse("2025-01-01T10:00:00Z");
        private static final Instant AUCTION_END = Instant.parse("2025-01-01T20:00:00Z");
        private static final Instant DURING_AUCTION = Instant.parse("2025-01-01T15:00:00Z");

        private static void assertSuccess(BidResult result, Money expectedPreviousAmount,
            Money expectedNewAmount, UUID expectedPreviousBidder) {
            assertThat(result).isExactlyInstanceOf(Success.class);
            Success successResult = (Success) result;

            assertThat(successResult.previousAmount()).isEqualTo(expectedPreviousAmount);
            assertThat(successResult.newAmount()).isEqualTo(expectedNewAmount);
            assertThat(successResult.previousBidder()).isEqualTo(expectedPreviousBidder);
        }

        private static void assertAuctionBidState(Auction auction, UUID expectedWinnerId,
            Money expectedHighestBid, long expectedBidVersion) {
            assertThat(auction.getCurrentWinnerId()).isEqualTo(expectedWinnerId);
            assertThat(auction.getHighestBid()).isEqualTo(expectedHighestBid);
            assertThat(auction.getBidVersion()).isEqualTo(expectedBidVersion);
        }

        @Test
        @DisplayName("Initial bid should return success")
        void shouldPlaceInitialBid() {
            // Given an auction without any bids
            Auction auction = new AuctionBuilder()
                .withTimeWindow(AUCTION_START, AUCTION_END)
                .build();

            // When a bidder places a bid
            UUID bidderId = UUID.randomUUID();
            Money bidAmount = new Money(10);
            BidResult result = auction.placeBid(bidderId, bidAmount, DURING_AUCTION);

            // Then a corresponding Success result should be returned and auction state updated
            assertSuccess(result, null, bidAmount, null);
            assertAuctionBidState(auction, bidderId, bidAmount, 1L);
        }

        @Test
        @DisplayName("New bid should return success with details about previous bid")
        void shouldPlaceNewBid() {
            // Given an auction with an existing bid
            Auction auction = new AuctionBuilder()
                .withTimeWindow(AUCTION_START, AUCTION_END)
                .build();
            UUID previousBidderId = UUID.randomUUID();
            Money previousBidAmount = new Money(10);
            Instant firstBidTime = Instant.parse("2025-01-01T14:00:00Z");
            auction.placeBid(previousBidderId, previousBidAmount, firstBidTime);

            // When a bidder places a new higher bid
            UUID newBidderId = UUID.randomUUID();
            Money newBidAmount = new Money(20);
            Instant secondBidTime = Instant.parse("2025-01-01T16:00:00Z");
            BidResult result = auction.placeBid(newBidderId, newBidAmount, secondBidTime);

            // Then a corresponding Success result should be returned and auction state updated
            assertSuccess(result, previousBidAmount, newBidAmount, previousBidderId);
            assertAuctionBidState(auction, newBidderId, newBidAmount, 2L);
        }

        @Test
        @DisplayName("Bid should not be placed on closed auction")
        void shouldNotPlaceBidOnClosedAuction() {
            // Given a closed auction with an existing bid
            UUID winnerId = UUID.randomUUID();
            Money winningBidAmount = new Money(10);
            Auction auction = new AuctionBuilder()
                .withTimeWindow(AUCTION_START, AUCTION_END)
                .build();
            auction.placeBid(winnerId, winningBidAmount, DURING_AUCTION);
            auction.end();

            // When another bid is attempted
            BidResult result = auction.placeBid(UUID.randomUUID(), new Money(20), DURING_AUCTION);

            // Then an AuctionNotActive result should be returned and auction state remains unchanged
            assertThat(result).isExactlyInstanceOf(AuctionNotActive.class);
            assertAuctionBidState(auction, winnerId, winningBidAmount, 1L);
        }

        @Test
        @DisplayName("Bid should not be placed on open auction past end time")
        void shouldNotPlaceBidOnOpenButEndedAuction() {
            // Given an open auction with an existing bid, that has passed its end time
            UUID winnerId = UUID.randomUUID();
            Money winningBidAmount = new Money(10);
            Instant winningBidTime = Instant.parse("2025-01-01T15:00:00Z");
            Auction auction = new AuctionBuilder()
                .withTimeWindow(AUCTION_START, AUCTION_END)
                .build();
            auction.placeBid(winnerId, winningBidAmount, winningBidTime);

            // When another bid is attempted after the auction end time
            Instant afterEnd = AUCTION_END.plusSeconds(1L);
            BidResult result = auction.placeBid(UUID.randomUUID(), new Money(20), afterEnd);

            // Then an AuctionNotActive result should be returned and auction state remains unchanged
            assertThat(result).isExactlyInstanceOf(AuctionNotActive.class);
            assertAuctionBidState(auction, winnerId, winningBidAmount, 1L);
        }

        @Test
        @DisplayName("Bid should not be placed when it is lower than the current highest bid")
        void shouldNotPlaceBidLowerThanCurrent() {
            // Given an open auction with an existing bid
            UUID lastBidderId = UUID.randomUUID();
            Money lastBidAmount = new Money(10);
            Instant lastBidTime = Instant.parse("2025-01-01T15:00:00Z");
            Auction auction = new AuctionBuilder()
                .withTimeWindow(AUCTION_START, AUCTION_END)
                .build();
            auction.placeBid(lastBidderId, lastBidAmount, lastBidTime);

            // When another bid is attempted after the previous one, with a lower amount
            Instant validTime = lastBidTime.plusSeconds(1L);
            BidResult result = auction.placeBid(UUID.randomUUID(), new Money(5), validTime);

            // Then a BidAmountTooLow result should be returned and auction state remains unchanged
            assertThat(result).isExactlyInstanceOf(BidAmountTooLow.class);
            assertAuctionBidState(auction, lastBidderId, lastBidAmount, 1L);
        }

        @Test
        @DisplayName("Bid should not be placed when it is lower than the starting price")
        void shouldNotPlaceBidLowerThanStarting() {
            // Given an auction without any bids
            Auction auction = new AuctionBuilder()
                .withTimeWindow(AUCTION_START, AUCTION_END)
                .withStartingPrice(BigDecimal.valueOf(100))
                .build();

            // When a bidder places a bid, lower than the starting price
            UUID bidderId = UUID.randomUUID();
            Money bidAmount = new Money(10);
            BidResult result = auction.placeBid(bidderId, bidAmount, DURING_AUCTION);

            // Then a BidAmountTooLow result should be returned and auction state remains unchanged
            assertThat(result).isExactlyInstanceOf(BidAmountTooLow.class);
            assertAuctionBidState(auction, null, null, 0L);
        }
    }
}