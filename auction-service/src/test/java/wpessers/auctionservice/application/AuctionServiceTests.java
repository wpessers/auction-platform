package wpessers.auctionservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.out.AuctionStoragePort;
import wpessers.auctionservice.domain.model.Auction;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTests {

    @Mock
    private AuctionStoragePort auctionStoragePort;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    @DisplayName("Should save auction with fields given through command")
    void shouldSaveAuction() {
        // Given
        CreateAuctionCommand command = new CreateAuctionCommand(
            "Mona Lisa",
            "16th Century painting by Leonardo da Vinci",
            Instant.now().plusSeconds(60),
            10
        );

        // When
        UUID auctionId = auctionService.createAuction(command);

        // Then
        assertThat(auctionId).isNotNull();
        verify(auctionStoragePort).save(any(Auction.class));
    }
}