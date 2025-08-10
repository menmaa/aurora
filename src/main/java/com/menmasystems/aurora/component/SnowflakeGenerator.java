package com.menmasystems.aurora.component;

import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SnowflakeGenerator {
    private static final long EPOCH = 1704067200000L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long PROCESS_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + PROCESS_ID_BITS + WORKER_ID_BITS;
    private static final long PROCESS_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_PROCESS_ID = ~(-1L << PROCESS_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private final long processId;
    private final AtomicLong sequence = new AtomicLong(0L);
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private final ConcurrentHashMap<Long, AtomicLong> threadWaitCount = new ConcurrentHashMap<>();

    private final SnowflakeAllocator snowflakeAllocator;

    public SnowflakeGenerator(SnowflakeAllocator snowflakeAllocator) {
        this.snowflakeAllocator = snowflakeAllocator;
        this.processId = 0;
    }

    public synchronized SnowflakeId generate() {
        int workerId = snowflakeAllocator.getWorkerId();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastTimestamp.get();

        if (currentTime < lastTime) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        long seq;
        if (currentTime == lastTime) {
            seq = sequence.incrementAndGet() & SEQUENCE_MASK;
            if (seq == 0) {
                currentTime = waitForNextMillis(lastTime);
            }
        } else {
            sequence.set(0L);
            seq = 0;
        }
        lastTimestamp.set(currentTime);

        return new SnowflakeId(((currentTime - EPOCH) << TIMESTAMP_SHIFT)
                | ((processId & MAX_PROCESS_ID) << PROCESS_ID_SHIFT)
                | ((workerId & MAX_WORKER_ID) << WORKER_ID_SHIFT)
                | seq);
    }

    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        long waitCount = threadWaitCount.computeIfAbsent(lastTimestamp, k -> new AtomicLong(0)).incrementAndGet();
        while (timestamp <= lastTimestamp) {
            try {
                Thread.sleep(waitCount); // Wait for existing thread wait count + 1 millisecond
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                throw new RuntimeException("Interrupted while waiting for next millisecond", e);
            }
            timestamp = System.currentTimeMillis();
        }
        threadWaitCount.remove(lastTimestamp); // Clean up the wait count for the past timestamp
        return timestamp;
    }
}
