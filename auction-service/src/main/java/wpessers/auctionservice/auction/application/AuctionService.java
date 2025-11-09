package wpessers.auctionservice.auction.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import wpessers.auctionservice.auction.application.port.in.AuctionResponse;
import wpessers.auctionservice.auction.application.port.in.CreateAuctionCommand;
import wpessers.auctionservice.auction.application.port.out.AuctionStorage;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.AuctionStatus;
import wpessers.auctionservice.auction.domain.AuctionWindow;
import wpessers.auctionservice.auction.domain.exception.AuctionNotFoundException;
import wpessers.auctionservice.shared.application.port.out.IdGenerator;
import wpessers.auctionservice.shared.application.port.out.TimeProvider;
import wpessers.auctionservice.shared.domain.Money;

@Service
public class AuctionService {

    private final IdGenerator idGenerator;
    private final AuctionStorage auctionStorage;
    private final TimeProvider timeProvider;
    private final AuctionMapper mapper;

    public AuctionService(
        IdGenerator idGenerator,
        AuctionStorage auctionStorage,
        TimeProvider timeProvider, AuctionMapper mapper
    ) {
        this.idGenerator = idGenerator;
        this.auctionStorage = auctionStorage;
        this.timeProvider = timeProvider;
        this.mapper = mapper;
    }

    public UUID createAuction(CreateAuctionCommand command) {
        UUID id = idGenerator.generateId();

        Instant startTime = timeProvider.now();
        AuctionStatus status = AuctionStatus.ACTIVE;
        if (command.startTime() != null && command.startTime().isAfter(startTime)) {
            startTime = command.startTime();
            status = AuctionStatus.SCHEDULED;
        }
        AuctionWindow auctionWindow = new AuctionWindow(startTime, command.endTime());

        Money startingPrice = new Money(command.startingPrice());
        Auction auction = new Auction(
            id,
            command.name(),
            command.description(),
            auctionWindow,
            startingPrice,
            status
        );
        auctionStorage.save(auction);
        return id;
    }

    public AuctionResponse findAuction(UUID auctionId) {
        return auctionStorage.findById(auctionId)
            .map(mapper::toResponse)
            .orElseThrow(() -> new AuctionNotFoundException(
                String.format("Auction with id %s not found", auctionId)));
    }

    public List<AuctionResponse> getActiveAuctions() {
        return auctionStorage.getActiveAuctions().map(mapper::toResponse).toList();
    }
}
