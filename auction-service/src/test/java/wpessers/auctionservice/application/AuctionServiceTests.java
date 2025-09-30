package wpessers.auctionservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.Auction;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTests {

    @Mock
    private AuctionStoragePort auctionStoragePort;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    void Create_Auction() {
        // Given
        CreateAuctionCommand createAuctionCommand = new CreateAuctionCommand("Mona Lisa",
            "16th Century painting by Leonardo da Vinci",
            Optional.of(Instant.parse("2025-01-01T01:00:00Z")));

        // When
        auctionService.createAuction(createAuctionCommand);

        // Then
        ArgumentCaptor<Auction> captor = ArgumentCaptor.forClass(Auction.class);
        verify(auctionStoragePort).createAuction(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(createAuctionCommand.name());
        assertThat(captor.getValue().getDescription()).isEqualTo(createAuctionCommand.description());
        assertThat(captor.getValue().getEndTime()).isEqualTo(createAuctionCommand.endTime().get());
    }
}