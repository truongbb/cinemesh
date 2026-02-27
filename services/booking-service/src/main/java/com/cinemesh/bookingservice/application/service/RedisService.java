package com.cinemesh.bookingservice.application.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisService {

    final StringRedisTemplate redisTemplate;

    @Value("${application.booking.seat-locking-ttl-in-sec}")
    int seatLockingTtl;

    private static final String KEY_PREFIX = "booking:lock:showtime:";

    private static final String LOCK_SCRIPT = """
            for i, key in ipairs(KEYS) do
                if redis.call('EXISTS', key) == 1 then
                    return 0
                end
            end
            for i, key in ipairs(KEYS) do
                redis.call('SETEX', key, ARGV[2], ARGV[1])
            end
            return 1
            """;

    /**
     * Atomically locks multiple seats for a user.
     * Returns true if ALL seats were locked successfully, false if ANY seat was already taken.
     */
    public boolean lockSeats(UUID showtimeId, List<UUID> seatIds, UUID userId) {
        if (seatIds == null || seatIds.isEmpty()) return false;

        List<String> keys = buildKeys(showtimeId, seatIds);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LOCK_SCRIPT, Long.class);

        try {
            Long result = redisTemplate.execute(redisScript, keys, userId.toString(), seatLockingTtl + "");
            return result != null && result == 1L;
        } catch (Exception e) {
            log.error("Redis execution failed during atomic seat locking for showtime {}", showtimeId, e);
            return false;
        }
    }

    /**
     * Releases the locks for the specified seats.
     * Used when an order fails to save, a user cancels, or payment succeeds.
     */
    public void unlockSeats(UUID showtimeId, List<UUID> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) return;

        List<String> keys = buildKeys(showtimeId, seatIds);

        try {
            redisTemplate.delete(keys);
            log.info("Successfully released {} seat locks for showtime {}", keys.size(), showtimeId);
        } catch (Exception e) {
            log.error("Failed to manually release Redis locks. Keys will expire automatically.", e);
        }
    }

    /**
     * Highly performant O(1) bulk fetch to see which seats are currently locked.
     * Uses Redis MGET to avoid the N+1 query problem.
     */
    public Set<UUID> getLockedSeatIds(UUID showtimeId, List<UUID> allSeatIdsInRoom) {
        if (allSeatIdsInRoom == null || allSeatIdsInRoom.isEmpty()) return new HashSet<>();

        List<String> keys = buildKeys(showtimeId, allSeatIdsInRoom);
        Set<UUID> lockedSeatIds = new HashSet<>();

        try {
            // multiGet fetches all keys in a single network trip
            List<String> values = redisTemplate.opsForValue().multiGet(keys);

            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    // If the value is not null, the seat is currently locked
                    if (values.get(i) != null) {
                        lockedSeatIds.add(allSeatIdsInRoom.get(i));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch locked seats from Redis for showtime {}", showtimeId, e);
            // If Redis fails, we gracefully return an empty set rather than breaking the whole UI
        }

        return lockedSeatIds;
    }

    // Helper method to keep key generation consistent
    private List<String> buildKeys(UUID showtimeId, List<UUID> seatIds) {
        return seatIds.stream()
                .map(seatId -> KEY_PREFIX + showtimeId + ":seat:" + seatId)
                .toList();
    }

}
