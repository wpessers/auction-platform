package wpessers.auctionservice.infrastructure.out.generation.fixed;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import wpessers.auctionservice.application.port.out.AuctionIdGenerator;

public class StubAuctionIdGeneratorAdapter implements AuctionIdGenerator {

    private final Queue<UUID> ids;

    public StubAuctionIdGeneratorAdapter() {
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
