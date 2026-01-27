package wpessers.auctionservice.user.infrastructure.in.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wpessers.auctionservice.shared.application.port.out.TokenParser;
import wpessers.auctionservice.shared.application.port.out.UserClaims;
import wpessers.auctionservice.shared.infrastructure.in.web.JwtAuthenticationFilter;
import wpessers.auctionservice.shared.infrastructure.in.web.SecurityConfig;
import wpessers.auctionservice.user.application.UserAuthService;
import wpessers.auctionservice.user.application.port.in.UserProfileResponse;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAuthService userAuthService;

    @MockitoBean
    private TokenParser tokenParser;

    @Test
    @DisplayName("Should return user profile when authenticated")
    void shouldReturnProfileWhenAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserClaims userClaims = new UserClaims(userId, "testuser");
        UserProfileResponse profileResponse = new UserProfileResponse(
            userId,
            "testuser",
            "test@email.com"
        );

        when(tokenParser.parseToken("valid-token")).thenReturn(userClaims);
        when(userAuthService.getProfile(userId)).thenReturn(profileResponse);

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.toString()))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    @DisplayName("Should return 403 when not authenticated")
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        // Spring Security returns 403 Forbidden for anonymous requests to protected endpoints
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isForbidden());
    }
}
