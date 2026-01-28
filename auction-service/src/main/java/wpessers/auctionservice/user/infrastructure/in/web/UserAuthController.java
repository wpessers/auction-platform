package wpessers.auctionservice.user.infrastructure.in.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wpessers.auctionservice.shared.application.port.out.UserClaims;
import wpessers.auctionservice.user.application.AuthTokens;
import wpessers.auctionservice.user.application.UserAuthService;
import wpessers.auctionservice.user.application.port.in.RegisterUserCommand;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokensResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        RegisterUserCommand registerUserCommand = new RegisterUserCommand(
            request.username(),
            request.password(),
            request.email()
        );

        AuthTokens tokens = userAuthService.register(registerUserCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(tokens));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokensResponse> login(@RequestBody @Valid LoginUserRequest request) {
        AuthTokens tokens = userAuthService.login(request.username(), request.password());
        return ResponseEntity.ok(toResponse(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokensResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AuthTokens tokens = userAuthService.refreshTokens(request.refreshToken());
        return ResponseEntity.ok(toResponse(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserClaims userClaims) {
        if (userClaims != null) {
            userAuthService.logout(userClaims.userId());
        }
        return ResponseEntity.noContent().build();
    }

    private AuthTokensResponse toResponse(AuthTokens tokens) {
        return new AuthTokensResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            tokens.expiresIn()
        );
    }
}
