package wpessers.auctionservice.auction.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.infrastructure.out.cache.inmemory.FakeAuctionRegistryAdapter;
import wpessers.auctionservice.auction.infrastructure.out.persistence.inmemory.FakeAuctionStorageAdapter;
import wpessers.auctionservice.fixtures.AuctionBuilder;

class RegistryInitializationServiceTest {

    private FakeAuctionRegistryAdapter registry;
    private FakeAuctionStorageAdapter storage;
    private RegistryInitializationService service;

    @BeforeEach
    void setUp() {
        registry = new FakeAuctionRegistryAdapter();
        storage = new FakeAuctionStorageAdapter();
        service = new RegistryInitializationService(registry, storage);
    }

    @Test
    @DisplayName("Should initialize the auction registry with active auctions")
    void shouldInitializeRegistry() {
        Auction activeAuction = new AuctionBuilder().withStatus(AuctionStatus.ACTIVE).build();
        Auction closedAuction = new AuctionBuilder().withStatus(AuctionStatus.CLOSED).build();
        storage.save(activeAuction);
        storage.save(closedAuction);

        service.initializeRegistry();

        assertThat(registry.isRegistered(activeAuction.getId())).isTrue();
        assertThat(registry.isRegistered(closedAuction.getId())).isFalse();
    }
}