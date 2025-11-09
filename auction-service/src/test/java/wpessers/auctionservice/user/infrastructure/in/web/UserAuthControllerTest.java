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
import wpessers.auctionservice.shared.infrastructure.in.web.SecurityConfig;
import wpessers.auctionservice.user.application.UserAuthService;
import wpessers.auctionservice.user.application.port.in.RegisterUserCommand;
import wpessers.auctionservice.user.domain.exception.InvalidEmailException;
import wpessers.auctionservice.user.domain.exception.InvalidUsernameException;

@Import(SecurityConfig.class)
@WebMvcTest(UserAuthController.class)
class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAuthService userAuthService;

    @Test
    @DisplayName("Should register user and return CREATED status")
    void shouldRegisterUser() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
            "username",
            "password",
            "email@test.com"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should login user and return OK status")
    void shouldLoginUser() throws Exception {
        LoginUserRequest request = new LoginUserRequest(
            "username",
            "password"
        );
        String mockToken = "mock-jwt-token";
        when(userAuthService.login("username", "password")).thenReturn(mockToken);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(mockToken));
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