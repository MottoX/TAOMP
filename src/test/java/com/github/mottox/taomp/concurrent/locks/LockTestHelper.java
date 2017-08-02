package com.github.mottox.taomp.concurrent.locks;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;

import com.github.mottox.taomp.common.LockCounter;

/**
 * {@link SimpleLock}测试辅助工具类。
 *
 * @author Robin Wang
 */
public class LockTestHelper {

    public static void testLockAndUnlock(SimpleLock lock) throws Exception {
        // test single thread
        testLockAndUnlock(lock, 1);
        // test multiple threads
        testLockAndUnlock(lock, 4);
    }

    public static void testLockAndUnlock(SimpleLock lock, int thread) throws Exception {
        testLockAndUnlock(lock, thread, 500000);
    }

    public static void testLockAndUnlock(SimpleLock lock, int thread, int iteration) throws Exception {
        LockCounter lockCounter = new LockCounter(lock);
        ExecutorService service = Executors.newFixedThreadPool(thread);
        List<Callable<Integer>> tasks = Collections.nCopies(thread, () -> {
            for (int i = 0; i < iteration; i++) {
                lockCounter.getAndIncrement();
            }
            return iteration;
        });
        service.invokeAll(tasks);
        Assert.assertEquals((long) thread * iteration, lockCounter.get());
    }
}
