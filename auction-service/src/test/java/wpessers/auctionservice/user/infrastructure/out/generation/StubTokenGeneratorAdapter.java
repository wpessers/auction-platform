package wpessers.auctionservice.user.infrastructure.out.generation;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import wpessers.auctionservice.user.application.port.out.TokenGenerator;

public class StubTokenGeneratorAdapter implements TokenGenerator {

    private final Queue<String> tokens;

    public StubTokenGeneratorAdapter() {
        this.tokens = new ArrayDeque<>();
    }

    @Override
    public String generateToken(UUID userId, String username) {
        return tokens.remove();
    }

    public void addToken(String token) {
        tokens.add(token);
    }
}
