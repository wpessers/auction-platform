package wpessers.auctionservice.auction.infrastructure.in.web;

import jakarta.validation.Valid;
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
import wpessers.auctionservice.auction.application.AuctionService;
import wpessers.auctionservice.auction.application.port.in.AuctionResponse;
import wpessers.auctionservice.auction.application.port.in.CreateAuctionCommand;
import wpessers.auctionservice.bid.application.port.in.BidResponse;
import wpessers.auctionservice.bid.application.port.out.BidStorage;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidStorage bidStorage;

    public AuctionController(AuctionService auctionService, BidStorage bidStorage) {
        this.auctionService = auctionService;
        this.bidStorage = bidStorage;
    }

    @PostMapping
    public ResponseEntity<UUID> createAuction(@RequestBody @Valid CreateAuctionRequest request) {
        CreateAuctionCommand createAuctionCommand = new CreateAuctionCommand(
            request.name(),
            request.description(),
            request.startTime(),
            request.endTime(),
            request.startingPrice()
        );

        UUID auctionId = auctionService.createAuction(createAuctionCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionId);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AuctionResponse>> getActiveAuctions() {
        return ResponseEntity.ok(auctionService.getActiveAuctions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.findAuction(id));
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<List<BidResponse>> getBidHistory(@PathVariable UUID id) {
        List<BidResponse> bids = bidStorage.findByAuctionId(id)
            .stream()
            .map(bid -> new BidResponse(
                bid.bidderId(),
                bid.amount().amount(),
                bid.timestamp()
            ))
            .toList();
        return ResponseEntity.ok(bids);
    }
}
