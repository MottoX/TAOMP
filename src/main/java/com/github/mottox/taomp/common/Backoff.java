package com.github.mottox.taomp.common;

import java.util.Random;

/**
 * 该类封装线程后退的逻辑。
 * 为保证争用的并发线程在同一时刻不会反复地尝试获得锁，让线程后退一个随机的时间间隔。每次线程尝试得到一个锁并失败后，就把期望的后退时间加倍，
 * 直到达到一个固定的最大值。
 *
 * @author Robin Wang
 */
public class Backoff {

    private final int minDelay;

    private final int maxDelay;

    private final Random random;

    private int limit;

    public Backoff(int minDelay, int maxDelay) {
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.limit = minDelay;
        this.random = new Random();
    }

    public void backoff() throws InterruptedException {
        int delay = random.nextInt(limit);
        limit = Math.min(maxDelay, 2 * limit);
        Thread.sleep(delay);
    }
}
