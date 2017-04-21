package com.github.mottox.taomp.concurrent.locks;

import org.junit.Test;

/**
 * {@link TTASLock}测试类。
 *
 * @author Robin Wang
 */
public class TTASLockTest {

    @Test(timeout = 500)
    public void testLockAndUnlock() throws Exception {
        LockTestHelper.testLockAndUnlock(new TTASLock());
    }
}