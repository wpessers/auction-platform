package wpessers.auctionservice.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.model.Auction;
import java.time.Instant;
import java.util.UUID;

import static java.util.UUID.randomUUID;


@Service
public class AuctionService {

    private final AuctionStoragePort auctionStoragePort;

    public AuctionService(AuctionStoragePort auctionStoragePort) {
        this.auctionStoragePort = auctionStoragePort;
    }

    public UUID createAuction(CreateAuctionCommand command) {
        UUID id = randomUUID();
        Auction auction = Auction.create(
            id,
            command.name(),
            command.description(),
            Instant.now(),
            command.endTime()
        );
        auctionStoragePort.save(auction);
        return id;
    }
}
