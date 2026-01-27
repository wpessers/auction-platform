package wpessers.auctionservice.bid.infrastructure.in.spring;

import java.math.BigDecimal;
import java.util.UUID;

public record OutbidMessage(UUID uuid, String auctionName, BigDecimal amount) {

}
