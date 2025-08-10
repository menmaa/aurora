/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ReactiveRedisWorkerIdSnowflakeAllocator implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(ReactiveRedisWorkerIdSnowflakeAllocator.class);

    private static final String WORKER_KEY_PREFIX = "snowflake:workers:";
    private static final int MAX_WORKER_ID = 31; // 5 bits -> 0..31

    private static final Duration LEASE_TTL = Duration.ofSeconds(30);
    private static final Duration RENEW_INTERVAL = Duration.ofSeconds(10);
    private static final Duration MAX_JITTER = Duration.ofMillis(500); // +/- 500ms jitter

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ConfigurableApplicationContext context;

    private final AtomicInteger assignedWorkerId = new AtomicInteger(-1);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Disposable renewalDisposable; // used to cancel the renewal loop

    public ReactiveRedisWorkerIdSnowflakeAllocator(ReactiveStringRedisTemplate redisTemplate, ConfigurableApplicationContext context) {
        this.redisTemplate = redisTemplate;
        this.context = context;
    }

    private Mono<Integer> acquireWorkerId() {
        return tryAllocateRange(0, MAX_WORKER_ID)
                .switchIfEmpty(Mono.error(() -> new IllegalStateException("No available Snowflake Worker IDs")))
                .flatMap(id -> startRenewalLoop(id).thenReturn(id));
    }

    private Mono<Integer> tryAllocateRange(int from, int to) {
        return Mono.defer(() -> tryAllocateId(from))
                .switchIfEmpty(
                        (from + 1 <= to) ? tryAllocateRange(from + 1, to) : Mono.empty()
                );
    }

    private Mono<Integer> tryAllocateId(int id) {
        return redisTemplate.opsForValue()
                .setIfAbsent(workerKey(id), "1", LEASE_TTL)
                .flatMap(success -> {
                    if(Boolean.TRUE.equals(success)) {
                        assignedWorkerId.set(id);
                        return Mono.just(id);
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> startRenewalLoop(int id) {
        if(assignedWorkerId.get() != id) {
            return Mono.error(new IllegalStateException("Assigned Snowflake Worker ID mismatch"));
        }
        disposeRenewal();

        // noinspection unused
        renewalDisposable = Mono.defer(() -> Mono.delay(withJitter(RENEW_INTERVAL)))
                .repeat()
                .flatMap(tick -> !running.get()
                    ? Mono.empty()
                    : renewOnce(id)
                            .onErrorResume(err -> {
                                log.error("Failed to renew Snowflake Worker ID: {}", id, err);
                                return Mono.empty();
                            })
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(ok ->
                    log.debug("Renewal loop for Snowflake Worker ID: {} is running", id),
                        err -> markLost(), this::markLost);

        return Mono.empty();
    }

    private Mono<Boolean> renewOnce(int id) {
        return redisTemplate.expire(workerKey(id), LEASE_TTL)
                .flatMap(success -> {
                    if(Boolean.TRUE.equals(success)) {
                        log.info("Renewed Snowflake Worker ID: {}", id);
                        return Mono.just(true);
                    }
                    return Mono.error(new IllegalStateException("Failed to renew lease for Snowflake Worker ID: " + id));
                });
    }

    private Duration withJitter(Duration base) {
        long baseMillis = base.toMillis();
        long jitter = ThreadLocalRandom.current().nextLong(-MAX_JITTER.toMillis(), MAX_JITTER.toMillis() + 1);
        return Duration.ofMillis(Math.max(1, baseMillis + jitter));
    }

    private void markLost() {
        assignedWorkerId.set(-1);
        disposeRenewal();
        log.error("Snowflake Worker ID Allocation lost. ID generation will be interrupted if not reallocated.");
    }

    private Mono<Void> releaseWorkerId() {
        int id = assignedWorkerId.getAndSet(-1);
        if(id < 0) {
            return Mono.empty();
        }

        return redisTemplate.delete(workerKey(id)).then();
    }

    private void disposeRenewal() {
        if (renewalDisposable != null && !renewalDisposable.isDisposed()) {
            renewalDisposable.dispose();
        }
        renewalDisposable = null;
    }

    @Override
    public void start() {
        if(!running.compareAndSet(false, true)) {
            return;
        }

        acquireWorkerId()
                .doOnError(err -> {
                    log.error("Failed to allocate Snowflake Worker ID", err);
                    int exitCode = SpringApplication.exit(context, () -> 1);
                    System.exit(exitCode);
                })
                .subscribe(id -> log.info("Allocated Snowflake Worker ID: {}", id));
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            disposeRenewal();

            int workerId = assignedWorkerId.get();
            if(workerId < 0) {
                return;
            }

            long startTime = System.nanoTime();
            try {
                log.info("Releasing Snowflake Worker ID: {}...", workerId);

                releaseWorkerId().subscribeOn(Schedulers.boundedElastic()).block(Duration.ofSeconds(1));

                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
                log.info("Snowflake Worker ID: {} released in {} ms", workerId, elapsedMs);
            } catch (Exception e) {
                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
                log.warn("Failed to release Snowflake Worker ID: {} (after {} ms)", workerId, elapsedMs, e);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    public int getWorkerId() {
        return assignedWorkerId.get();
    }

    public boolean isAllocated() {
        return assignedWorkerId.get() != -1;
    }

    private static String workerKey(int id) {
        return WORKER_KEY_PREFIX + id;
    }
}
