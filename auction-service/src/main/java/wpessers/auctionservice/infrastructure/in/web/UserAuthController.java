package wpessers.auctionservice.infrastructure.in.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wpessers.auctionservice.application.port.in.command.RegisterUserCommand;
import wpessers.auctionservice.application.user.UserAuthService;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserRequest request) {
        RegisterUserCommand registerUserCommand = new RegisterUserCommand(
            request.username(),
            request.password(),
            request.email()
        );

        userAuthService.register(
            registerUserCommand);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginUserRequest request) {
        String token = userAuthService.login(request.username(), request.password());
        return ResponseEntity.ok(token);
    }
}
