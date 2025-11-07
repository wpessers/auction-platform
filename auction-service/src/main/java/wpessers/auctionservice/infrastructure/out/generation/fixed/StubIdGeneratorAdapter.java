package wpessers.auctionservice.infrastructure.out.generation.fixed;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import wpessers.auctionservice.application.port.out.IdGenerator;

public class StubIdGeneratorAdapter implements IdGenerator {

    private final Queue<UUID> ids;

    public StubIdGeneratorAdapter() {
        this.ids = new ArrayDeque<>();
    }

    @Override
    public UUID generateId() {
        return ids.remove();
    }

    public void addId(UUID id) {
        ids.add(id);
    }
}
