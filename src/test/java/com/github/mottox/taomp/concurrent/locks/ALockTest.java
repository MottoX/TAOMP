package com.github.mottox.taomp.concurrent.locks;

import org.junit.Test;

/**
 * {@link ALock}测试类。
 *
 * @author Robin Wang
 */
public class ALockTest {

    @Test(timeout = 10000)
    public void testLockAndUnlock() throws Exception {
        LockTestHelper.testLockAndUnlock(new ALock(8));
    }
}