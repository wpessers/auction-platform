package wpessers.auctionservice.infrastructure.out.generation.fixed;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import wpessers.auctionservice.application.port.out.TokenGenerator;

public class StubTokenGeneratorAdapter implements TokenGenerator {

    private final Queue<String> tokens;

    public StubTokenGeneratorAdapter() {
        this.tokens = new ArrayDeque<>();
    }

    @Override
    public String generateToken(UUID userId) {
        return tokens.remove();
    }

    public void addToken(String token) {
        tokens.add(token);
    }
}
