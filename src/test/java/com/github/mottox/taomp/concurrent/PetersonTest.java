package com.github.mottox.taomp.concurrent;

import org.junit.Assert;
import org.junit.Test;

import com.github.mottox.taomp.common.LockCounter;

/**
 * Peterson锁测试类。
 *
 * @author Robin Wang
 */
public class PetersonTest {

    @Test(timeout = 1000)
    public void testPeterson() throws Exception {
        LockCounter counter = new LockCounter(new Peterson());
        Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.getAndIncrement();
            }
        };
        Thread a = new Thread(runnable, "Thread a");
        Thread b = new Thread(runnable, "Thread b");
        a.start();
        b.start();
        a.join();
        b.join();
        Assert.assertEquals(2000, counter.get());
    }

}