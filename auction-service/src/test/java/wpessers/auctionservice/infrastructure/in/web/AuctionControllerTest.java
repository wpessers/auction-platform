package wpessers.auctionservice.infrastructure.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wpessers.auctionservice.application.auction.AuctionService;
import wpessers.auctionservice.application.port.in.command.CreateAuctionCommand;
import wpessers.auctionservice.application.port.in.query.AuctionResponse;
import wpessers.auctionservice.domain.exception.AuctionNotFoundException;

@WebMvcTest(AuctionController.class)
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuctionService auctionService;

    @Test
    @DisplayName("Should create auction and return CREATED status with auction id")
    void shouldCreateAuction() throws Exception {
        // Given
        CreateAuctionRequest request = new CreateAuctionRequest(
            "name",
            "desc",
            Instant.now().plusSeconds(60),
            BigDecimal.valueOf(100)
        );
        UUID createdId = UUID.randomUUID();
        when(auctionService.createAuction(any(CreateAuctionCommand.class))).thenReturn(createdId);

        // When & Then
        mockMvc.perform(post("/api/auctions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(createdId.toString()));
    }

    @Test
    @DisplayName("Should return OK status with auction details")
    void shouldReturnDetails() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        AuctionResponse auctionResponse = new AuctionResponse(
            id,
            "name",
            "desc",
            Instant.now(),
            Instant.now().plusSeconds(60),
            BigDecimal.valueOf(100)
        );
        when(auctionService.findAuction(id)).thenReturn(auctionResponse);

        // When & Then
        mockMvc.perform(get("/api/auctions/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(auctionResponse)));
    }

    @Test
    @DisplayName("Should return NOT_FOUND status when auction does not exist")
    void shouldReturnNotFound() throws Exception {
        // Given
        when(auctionService.findAuction(any(UUID.class))).thenThrow(AuctionNotFoundException.class);

        // When & Then
        mockMvc.perform(get("/api/auctions/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return OK status with active auctions")
    void shouldReturnActiveAuctions() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        List<AuctionResponse> response = List.of(new AuctionResponse(
            id,
            "name",
            "desc",
            Instant.now(),
            Instant.now().plusSeconds(60),
            BigDecimal.valueOf(100)
        ));
        when(auctionService.getActiveAuctions()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/auctions/active", id))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}