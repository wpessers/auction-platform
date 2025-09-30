package wpessers.auctionservice.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.Auction;


@Service
public class AuctionService {

    private AuctionStoragePort auctionStoragePort;

    public AuctionService(AuctionStoragePort auctionStoragePort) {
        this.auctionStoragePort = auctionStoragePort;
    }

    public void createAuction(CreateAuctionCommand createAuctionCommand) {
        auctionStoragePort.createAuction(new Auction("", "", "", "", 0));
    }
}
