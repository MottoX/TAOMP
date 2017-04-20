package com.github.mottox.taomp.concurrent;

import org.junit.Test;

/**
 * {@link BackoffLock}测试类。
 *
 * @author Robin Wang
 */
public class BackoffLockTest {

    @Test(timeout = 500)
    public void testBackoffLock() throws Exception {
        LockTestHelper.testFunctionality(new BackoffLock());
    }
}