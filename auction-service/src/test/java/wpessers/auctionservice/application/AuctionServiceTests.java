package wpessers.auctionservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Should create auction with fields given through command")
    void shouldCreateAuction() {
        // Given
        CreateAuctionCommand command = new CreateAuctionCommand("Mona Lisa",
            "16th Century painting by Leonardo da Vinci",
            Instant.parse("2025-01-01T01:00:00Z"));

        // When
        UUID auctionId = auctionService.createAuction(command);

        // Then
        ArgumentCaptor<Auction> captor = ArgumentCaptor.forClass(Auction.class);
        verify(auctionStoragePort).save(captor.capture());

        Auction saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(command.name());
        assertThat(saved.getDescription()).isEqualTo(command.description());
        assertThat(saved.getEndTime()).isEqualTo(command.endTime());
        assertThat(auctionId).isNotNull();
    }
}