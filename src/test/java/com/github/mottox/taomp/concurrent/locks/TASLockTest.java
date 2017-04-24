package com.github.mottox.taomp.concurrent.locks;

import org.junit.Test;

/**
 * {@link TASLock}测试类。
 *
 * @author Robin Wang
 */
public class TASLockTest {

    @Test
    public void testLockAndUnlock() throws Exception {
        LockTestHelper.testLockAndUnlock(new TASLock());
    }
}