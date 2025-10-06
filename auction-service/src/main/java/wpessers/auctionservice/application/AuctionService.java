package wpessers.auctionservice.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.in.query.AuctionResponse;
import wpessers.auctionservice.application.port.out.AuctionIdGenerator;
import wpessers.auctionservice.application.port.out.AuctionStorage;
import wpessers.auctionservice.application.port.out.TimeProvider;
import wpessers.auctionservice.domain.Auction;
import wpessers.auctionservice.domain.AuctionStatus;
import wpessers.auctionservice.domain.AuctionWindow;
import wpessers.auctionservice.domain.Money;
import wpessers.auctionservice.domain.exception.AuctionNotFoundException;

@Service
public class AuctionService {

    private final AuctionIdGenerator auctionIdGenerator;
    private final AuctionStorage auctionStorage;
    private final TimeProvider timeProvider;
    private final AuctionMapper mapper;

    public AuctionService(
        AuctionIdGenerator auctionIdGenerator,
        AuctionStorage auctionStorage,
        TimeProvider timeProvider, AuctionMapper mapper
    ) {
        this.auctionIdGenerator = auctionIdGenerator;
        this.auctionStorage = auctionStorage;
        this.timeProvider = timeProvider;
        this.mapper = mapper;
    }

    public UUID createAuction(CreateAuctionCommand command) {
        UUID id = auctionIdGenerator.generateId();

        Instant startTime = timeProvider.now();
        AuctionStatus status = AuctionStatus.OPEN;
        if (command.startTime() != null && !command.startTime().isBefore(startTime)) {
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
