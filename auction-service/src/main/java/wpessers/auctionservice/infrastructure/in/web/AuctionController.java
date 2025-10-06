package wpessers.auctionservice.infrastructure.in.web;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wpessers.auctionservice.application.AuctionService;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.in.query.AuctionResponse;

@RestController
@RequestMapping("/api/auctions")
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
            null,
            request.endTime(),
            request.startingPrice()
        );

        UUID auction = auctionService.createAuction(createAuctionCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(auction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.findAuction(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AuctionResponse>> getActiveAuctions() {
        return ResponseEntity.ok(auctionService.getActiveAuctions());
    }

}
