package wpessers.auctionservice.user.infrastructure.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wpessers.auctionservice.shared.application.port.out.UserClaims;
import wpessers.auctionservice.user.application.UserAuthService;
import wpessers.auctionservice.user.application.port.in.UserProfileResponse;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserAuthService userAuthService;

    public UserProfileController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
        @AuthenticationPrincipal UserClaims userClaims
    ) {
        UserProfileResponse profile = userAuthService.getProfile(userClaims.userId());
        return ResponseEntity.ok(profile);
    }
}
