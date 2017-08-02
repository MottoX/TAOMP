package com.github.mottox.taomp.concurrent.locks;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TOLock}测试类。
 *
 * @author Robin Wang
 */
public class TOLockTest {

    @Test(timeout = 10000)
    public void testLockAndUnlock() throws Exception {
        LockTestHelper.testLockAndUnlock(new TOLock());
    }

    @Test(timeout = 10000)
    public void testTryLockSuccess() throws Exception {
        TOLock lock = new TOLock();

        Thread tryLockThread = new Thread(() -> {
            try {
                boolean result = lock.tryLock(1000, TimeUnit.MILLISECONDS);
                Assert.assertEquals(true, result);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "the thread that tries to lock the lock");

        tryLockThread.start();
        tryLockThread.join();
    }

    @Test(timeout = 10000)
    public void testTryLockFail() throws Exception {
        TOLock lock = new TOLock();
        Thread holdThread = new Thread(lock::lock, "the thread  that holds the lock");

        Thread tryLockThread = new Thread(() -> {
            try {
                boolean result = lock.tryLock(1000, TimeUnit.MILLISECONDS);
                Assert.assertEquals(false, result);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "the thread that tries to lock the lock");

        holdThread.start();
        tryLockThread.start();

        holdThread.join();
        tryLockThread.join();
    }

}