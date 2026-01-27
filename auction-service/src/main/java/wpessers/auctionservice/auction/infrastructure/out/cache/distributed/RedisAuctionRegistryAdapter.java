package wpessers.auctionservice.auction.infrastructure.out.cache.distributed;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.port.out.AuctionRegistry;
import wpessers.auctionservice.auction.domain.Auction;
import wpessers.auctionservice.auction.domain.exception.AuctionNotFoundException;

/**
 * Redis-based implementation of AuctionRegistry for distributed deployments.
 * Uses Redis for auction storage and distributed locking to ensure thread-safe
 * bid operations across multiple application instances.
 */
@Component
@Profile("prod")
public class RedisAuctionRegistryAdapter implements AuctionRegistry {

    private static final Logger log = LoggerFactory.getLogger(RedisAuctionRegistryAdapter.class);
    private static final String AUCTION_KEY_PREFIX = "auction:";
    private static final String LOCK_KEY_PREFIX = "auction:lock:";
    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration LOCK_RETRY_DELAY = Duration.ofMillis(50);
    private static final int MAX_LOCK_RETRIES = 100;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisAuctionRegistryAdapter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void register(Auction auction) {
        String key = auctionKey(auction.getId());
        AuctionCacheDto dto = AuctionCacheDto.fromAuction(auction);
        redisTemplate.opsForValue().set(key, dto);
        log.debug("Registered auction {} in Redis cache", auction.getId());
    }

    @Override
    public void deregister(UUID auctionId) {
        String key = auctionKey(auctionId);
        redisTemplate.delete(key);
        log.debug("Deregistered auction {} from Redis cache", auctionId);
    }

    @Override
    public <T> T executeOnAuction(UUID auctionId, Function<Auction, T> action) {
        String lockKey = lockKey(auctionId);
        String auctionKey = auctionKey(auctionId);
        String lockValue = UUID.randomUUID().toString();

        boolean lockAcquired = acquireLock(lockKey, lockValue);
        if (!lockAcquired) {
            throw new IllegalStateException("Failed to acquire lock for auction " + auctionId);
        }

        try {
            Object cached = redisTemplate.opsForValue().get(auctionKey);
            if (cached == null) {
                throw new AuctionNotFoundException("Auction with id " + auctionId + " not found in cache");
            }

            AuctionCacheDto dto = convertToDto(cached);
            Auction auction = dto.toAuction();

            T result = action.apply(auction);

            AuctionCacheDto updatedDto = AuctionCacheDto.fromAuction(auction);
            redisTemplate.opsForValue().set(auctionKey, updatedDto);

            return result;
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    private boolean acquireLock(String lockKey, String lockValue) {
        for (int i = 0; i < MAX_LOCK_RETRIES; i++) {
            Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);
            if (Boolean.TRUE.equals(acquired)) {
                return true;
            }
            try {
                Thread.sleep(LOCK_RETRY_DELAY.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        log.warn("Failed to acquire lock for key {} after {} retries", lockKey, MAX_LOCK_RETRIES);
        return false;
    }

    private void releaseLock(String lockKey, String lockValue) {
        Object currentValue = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }

    private AuctionCacheDto convertToDto(Object cached) {
        if (cached instanceof AuctionCacheDto dto) {
            return dto;
        }
        throw new IllegalStateException("Unexpected cached object type: " + cached.getClass());
    }

    private String auctionKey(UUID auctionId) {
        return AUCTION_KEY_PREFIX + auctionId;
    }

    private String lockKey(UUID auctionId) {
        return LOCK_KEY_PREFIX + auctionId;
    }
}
