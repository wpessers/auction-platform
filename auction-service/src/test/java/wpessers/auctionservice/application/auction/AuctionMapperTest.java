package wpessers.auctionservice.application.auction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.application.port.in.query.AuctionResponse;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.fixtures.AuctionBuilder;

class AuctionMapperTest {

    private final AuctionMapper mapper = new AuctionMapper();

    @Test
    @DisplayName("Should map Auction domain object to AuctionResponse")
    void shouldMapAuctionToResponse() {
        Auction auction = new AuctionBuilder().build();

        AuctionResponse response = mapper.toResponse(auction);

        assertThat(response.id()).isEqualTo(auction.getId());
        assertThat(response.name()).isEqualTo(auction.getName());
        assertThat(response.description()).isEqualTo(auction.getDescription());
        assertThat(response.startTime()).isEqualTo(auction.getAuctionWindow().startTime());
        assertThat(response.endTime()).isEqualTo(auction.getAuctionWindow().endTime());
        assertThat(response.startingPrice()).isEqualTo(auction.getStartingPrice().amount());
    }
}