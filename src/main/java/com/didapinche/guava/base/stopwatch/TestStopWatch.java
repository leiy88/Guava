package com.didapinche.guava.base.stopwatch;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/7/3.
 */
public class TestStopWatch {
    public static void main(String[] args) throws InterruptedException {
        // 传统的记录耗时
        long start = System.currentTimeMillis();
        Thread.sleep(300);
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - start) + "ms");

        start = System.currentTimeMillis();
        Thread.sleep(1500);
        end = System.currentTimeMillis();
        System.out.println("cost:" + (end - start)/1000.0 + "s");

        // 使用stopwatch
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread.sleep(300);
        System.out.println("cost:" + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));

        stopwatch.reset().start();
        Thread.sleep(1500);
        System.out.println("cost:" + stopwatch.stop().elapsed(TimeUnit.SECONDS) + "s");
    }
}
