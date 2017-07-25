package com.github.mottox.taomp.concurrent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;

/**
 * 用于测试并发List的工具类。
 *
 * @author Robin Wang
 */
public class ConcurrentListTestHelper {

    private static ExecutorService service = Executors.newFixedThreadPool(4);

    public static void testAdd(ConcurrentList<String> list) throws Exception {
        List<Future<Integer>> futures = service.invokeAll(Collections.nCopies(8, (Callable<Integer>) () -> {
            int result = 0;
            for (int i = 0; i < 1000; i++) {
                boolean succ = list.add("add " + i);
                result += succ ? 1 : 0;
            }
            return result;
        }));

        int sum = 0;
        for (Future<Integer> future : futures) {
            sum += future.get();
        }
        Assert.assertEquals(1000, sum);
    }

    public static void testRemove(ConcurrentList<String> list) throws Exception {
        for (int i = 0; i < 1000; i++) {
            list.add("add " + i);
        }

        List<Future<Integer>> futures = service.invokeAll(Collections.nCopies(8, (Callable<Integer>) () -> {
            int result = 0;
            for (int i = 0; i < 1000; i++) {
                boolean succ = list.remove("add " + i);
                result += succ ? 1 : 0;
            }
            return result;
        }));

        int sum = 0;
        for (Future<Integer> future : futures) {
            sum += future.get();
        }

        Assert.assertEquals(1000, sum);
    }

}
