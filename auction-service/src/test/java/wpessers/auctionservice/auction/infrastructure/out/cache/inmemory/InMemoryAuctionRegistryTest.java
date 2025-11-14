package wpessers.auctionservice.auction.infrastructure.out.cache.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.exception.AuctionNotFoundException;
import wpessers.auctionservice.fixtures.AuctionBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryAuctionRegistryTest {

    private InMemoryAuctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new InMemoryAuctionRegistry();
    }

    @Test
    @DisplayName("Should register auction and execute action on it")
    void shouldRegisterAndExecute() {
        UUID auctionId = UUID.randomUUID();
        Auction auction = new AuctionBuilder().withId(auctionId).withName("Test").build();
        registry.register(auction);

        String result = registry.executeOnAuction(auctionId, Auction::getName);

        assertThat(result).isEqualTo("Test");
    }

    @Test
    @DisplayName("Should throw AuctionNotFoundException when auction not registered")
    void shouldThrowOnAuctionNotRegistered() {
        assertThrows(AuctionNotFoundException.class,
                () -> registry.executeOnAuction(UUID.randomUUID(), a -> null));
    }

    @Test
    @DisplayName("Should deregister auction and throw exception on execute")
    void shouldDeregisterAuction() {
        UUID auctionId = UUID.randomUUID();
        Auction auction = new AuctionBuilder().withId(auctionId).withName("Test").build();
        registry.register(auction);

        registry.deregister(auctionId);

        assertThrows(AuctionNotFoundException.class,
                () -> registry.executeOnAuction(auctionId, a -> null));
    }
}