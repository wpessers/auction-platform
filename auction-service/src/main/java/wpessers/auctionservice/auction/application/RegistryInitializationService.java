package wpessers.auctionservice.auction.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;

@Component
public class RegistryInitializationService {

    private final AuctionRegistry registry;
    private final AuctionStorage storage;

    public RegistryInitializationService(AuctionRegistry registry, AuctionStorage storage) {
        this.registry = registry;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public void initializeRegistry() {
        storage.getActiveAuctions().forEach(registry::register);
    }
}
