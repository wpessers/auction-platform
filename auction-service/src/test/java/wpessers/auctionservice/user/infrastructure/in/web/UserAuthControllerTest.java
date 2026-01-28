package wpessers.auctionservice.user.infrastructure.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wpessers.auctionservice.shared.application.port.out.TokenParser;
import wpessers.auctionservice.shared.infrastructure.in.web.JwtAuthenticationFilter;
import wpessers.auctionservice.shared.infrastructure.in.web.SecurityConfig;
import wpessers.auctionservice.user.application.AuthTokens;
import wpessers.auctionservice.user.application.UserAuthService;
import wpessers.auctionservice.user.application.port.in.RegisterUserCommand;
import wpessers.auctionservice.user.domain.exception.InvalidEmailException;
import wpessers.auctionservice.user.domain.exception.InvalidRefreshTokenException;
import wpessers.auctionservice.user.domain.exception.InvalidUsernameException;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WebMvcTest(UserAuthController.class)
class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAuthService userAuthService;

    @MockitoBean
    private TokenParser tokenParser;

    @Test
    @DisplayName("Should register user and return CREATED status with tokens")
    void shouldRegisterUser() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
            "username",
            "password",
            "email@test.com"
        );
        AuthTokens mockTokens = new AuthTokens("mock-access-token", "mock-refresh-token", 900000);
        when(userAuthService.register(any(RegisterUserCommand.class))).thenReturn(mockTokens);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
            .andExpect(jsonPath("$.expiresIn").value(900000));
    }

    @Test
    @DisplayName("Should login user and return OK status with tokens")
    void shouldLoginUser() throws Exception {
        LoginUserRequest request = new LoginUserRequest(
            "username",
            "password"
        );
        AuthTokens mockTokens = new AuthTokens("mock-access-token", "mock-refresh-token", 900000);
        when(userAuthService.login("username", "password")).thenReturn(mockTokens);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    @DisplayName("Should refresh tokens and return OK status")
    void shouldRefreshTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        AuthTokens mockTokens = new AuthTokens("new-access-token", "new-refresh-token", 900000);
        when(userAuthService.refreshTokens("valid-refresh-token")).thenReturn(mockTokens);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when refresh token is invalid")
    void shouldReturnUnauthorizedOnInvalidRefreshToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");
        doThrow(InvalidRefreshTokenException.class)
            .when(userAuthService).refreshTokens("invalid-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return CONFLICT status when username is invalid")
    void shouldReturnConflictOnInvalidUsername() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
            "duplicate",
            "password",
            "email@test.com"
        );
        doThrow(InvalidUsernameException.class)
            .when(userAuthService).register(any(RegisterUserCommand.class));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return CONFLICT status when email is invalid")
    void shouldReturnConflictOnInvalidEmail() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
            "username",
            "password",
            "duplicate.email@test.com"
        );
        doThrow(InvalidEmailException.class)
            .when(userAuthService).register(any(RegisterUserCommand.class));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }
}