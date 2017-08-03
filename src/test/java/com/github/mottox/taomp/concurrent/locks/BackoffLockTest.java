package com.github.mottox.taomp.concurrent.locks;

import org.junit.Test;

/**
 * {@link BackoffLock}测试类。
 *
 * @author Robin Wang
 */
public class BackoffLockTest {

    @Test(timeout = 10000)
    public void testLockAndUnlock() throws Exception {
        LockTestHelper.testLockAndUnlock(new BackoffLock());
    }
}