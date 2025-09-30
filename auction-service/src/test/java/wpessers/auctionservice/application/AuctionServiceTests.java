package wpessers.auctionservice.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.Auction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTests {

    @Mock
    private AuctionStoragePort auctionStoragePort;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    void Create_Auction() {
        // Given
        CreateAuctionCommand createAuctionCommand = new CreateAuctionCommand();

        // When
        auctionService.createAuction(createAuctionCommand);

        // Then
        verify(auctionStoragePort).createAuction(any(Auction.class));
    }
}