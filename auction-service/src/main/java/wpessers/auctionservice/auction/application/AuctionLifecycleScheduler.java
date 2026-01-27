package wpessers.auctionservice.auction.application;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.infrastructure.out.persistence.jpa.AuctionEntityMapper;
import wpessers.auctionservice.auction.infrastructure.out.persistence.jpa.AuctionRepository;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;

/**
 * Scheduler that manages auction lifecycle transitions.
 *
 * Auctions transition through three states:
 * - SCHEDULED: Auction is created but waiting for start time
 * - ACTIVE: Auction is accepting bids
 * - CLOSED: Auction has ended
 *
 * This scheduler runs periodically to:
 * 1. Activate SCHEDULED auctions when their startTime is reached
 * 2. Close ACTIVE auctions when their endTime is reached
 */
@Component
public class AuctionLifecycleScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionLifecycleScheduler.class);

    private final AuctionRepository auctionRepository;
    private final AuctionEntityMapper entityMapper;
    private final AuctionRegistry auctionRegistry;
    private final AuctionStorage auctionStorage;
    private final TimeProvider timeProvider;

    public AuctionLifecycleScheduler(
        AuctionRepository auctionRepository,
        AuctionEntityMapper entityMapper,
        AuctionRegistry auctionRegistry,
        AuctionStorage auctionStorage,
        TimeProvider timeProvider
    ) {
        this.auctionRepository = auctionRepository;
        this.entityMapper = entityMapper;
        this.auctionRegistry = auctionRegistry;
        this.auctionStorage = auctionStorage;
        this.timeProvider = timeProvider;
    }

    /**
     * Activates SCHEDULED auctions whose start time has been reached.
     * Runs every 30 seconds.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void activateScheduledAuctions() {
        Instant now = timeProvider.now();
        List<Auction> auctionsToActivate = auctionRepository
            .findByStatusAndStartTimeLessThanEqual(AuctionStatus.SCHEDULED, now)
            .stream()
            .map(entityMapper::toDomain)
            .toList();

        for (Auction auction : auctionsToActivate) {
            try {
                auction.start();
                auctionStorage.save(auction);
                auctionRegistry.register(auction);
                log.info("Activated auction: {} (id={})", auction.getName(), auction.getId());
            } catch (Exception e) {
                log.error("Failed to activate auction {}: {}", auction.getId(), e.getMessage());
            }
        }

        if (!auctionsToActivate.isEmpty()) {
            log.info("Activated {} scheduled auction(s)", auctionsToActivate.size());
        }
    }

    /**
     * Closes ACTIVE auctions whose end time has been reached.
     * Runs every 30 seconds.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void closeExpiredAuctions() {
        Instant now = timeProvider.now();
        List<Auction> auctionsToClose = auctionRepository
            .findByStatusAndEndTimeLessThanEqual(AuctionStatus.ACTIVE, now)
            .stream()
            .map(entityMapper::toDomain)
            .toList();

        for (Auction auction : auctionsToClose) {
            try {
                // Execute on the in-memory registry version to get the latest bid state
                auctionRegistry.executeOnAuction(auction.getId(), registryAuction -> {
                    registryAuction.end();
                    auctionStorage.save(registryAuction);
                    log.info("Closed auction: {} (id={}, winner={}, highestBid={})",
                        registryAuction.getName(),
                        registryAuction.getId(),
                        registryAuction.getCurrentWinnerId(),
                        registryAuction.getHighestBid() != null
                            ? registryAuction.getHighestBid().amount()
                            : "no bids");
                    return null;
                });
                auctionRegistry.deregister(auction.getId());
            } catch (Exception e) {
                // If auction is not in registry, close from DB version
                try {
                    auction.end();
                    auctionStorage.save(auction);
                    log.info("Closed auction (not in registry): {} (id={})",
                        auction.getName(), auction.getId());
                } catch (Exception ex) {
                    log.error("Failed to close auction {}: {}", auction.getId(), ex.getMessage());
                }
            }
        }

        if (!auctionsToClose.isEmpty()) {
            log.info("Closed {} expired auction(s)", auctionsToClose.size());
        }
    }
}
