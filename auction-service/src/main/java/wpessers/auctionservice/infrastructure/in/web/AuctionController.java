package wpessers.auctionservice.infrastructure.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wpessers.auctionservice.application.AuctionService;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import java.util.UUID;

@RestController
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<UUID> createAuction(@RequestBody CreateAuctionRequest request) {
        CreateAuctionCommand createAuctionCommand = new CreateAuctionCommand(
            request.name(),
            request.description(),
            request.endTime(),
            request.startingPrice()
        );

        UUID auction = auctionService.createAuction(createAuctionCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(auction);
    }
}
