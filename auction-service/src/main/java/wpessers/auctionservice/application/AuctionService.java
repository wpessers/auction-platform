package wpessers.auctionservice.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.Auction;


@Service
public class AuctionService {

    private final AuctionStoragePort auctionStoragePort;

    public AuctionService(AuctionStoragePort auctionStoragePort) {
        this.auctionStoragePort = auctionStoragePort;
    }

    public void createAuction(CreateAuctionCommand command) {
        Auction auction = new Auction(
            command.name(),
            command.description(),
            command.endTime()
        );
        auctionStoragePort.createAuction(auction);
    }
}
