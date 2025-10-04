package wpessers.auctionservice.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;
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
        AuctionWindow auctionWindow = new AuctionWindow(Instant.now(), command.endTime());
        Money startingPrice = new Money(command.startingPrice());

        Auction auction = Auction.create(
            id,
            command.name(),
            command.description(),
            auctionWindow,
            startingPrice
        );
        auctionStoragePort.save(auction);
        return id;
    }
}
