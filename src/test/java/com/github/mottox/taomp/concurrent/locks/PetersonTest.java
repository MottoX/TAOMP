package com.github.mottox.taomp.concurrent.locks;

import org.junit.Assert;
import org.junit.Test;

import com.github.mottox.taomp.common.LockCounter;

/**
 * {@link Peterson}测试类。
 *
 * @author Robin Wang
 */
public class PetersonTest {

    @Test(timeout = 10000)
    public void testLockAndUnlock() throws Exception {
        LockCounter counter = new LockCounter(new Peterson());
        int num = 1000000;
        Runnable runnable = () -> {
            for (int i = 0; i < num; i++) {
                counter.getAndIncrement();
            }
        };
        Thread a = new Thread(runnable, "Thread a");
        Thread b = new Thread(runnable, "Thread b");
        a.start();
        b.start();
        a.join();
        b.join();
        Assert.assertEquals(num << 1, counter.get());
    }

}