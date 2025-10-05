package wpessers.auctionservice.application;

import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.out.TimeProvider;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStorage;
import wpessers.auctionservice.application.port.out.AuctionIdGenerator;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionService {

    private final AuctionIdGenerator auctionIdGenerator;
    private final AuctionStorage auctionStorage;
    private final TimeProvider timeProvider;

    public AuctionService(
        AuctionIdGenerator auctionIdGenerator,
        AuctionStorage auctionStorage,
        TimeProvider timeProvider
    ) {
        this.auctionIdGenerator = auctionIdGenerator;
        this.auctionStorage = auctionStorage;
        this.timeProvider = timeProvider;
    }

    public UUID createAuction(CreateAuctionCommand command) {
        UUID id = auctionIdGenerator.generateId();
        AuctionWindow auctionWindow = new AuctionWindow(timeProvider.now(), command.endTime());
        Money startingPrice = new Money(command.startingPrice());

        Auction auction = Auction.create(
            id,
            command.name(),
            command.description(),
            auctionWindow,
            startingPrice
        );
        auctionStorage.save(auction);
        return id;
    }

    public List<Auction> getActiveAuctions() {
        return auctionStorage.getActiveAuctions();
    }
}
