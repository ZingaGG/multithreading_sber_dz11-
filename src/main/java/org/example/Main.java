package org.example;

import org.example.ThreadPools.FixedThreadPool;
import org.example.ThreadPools.ScalableThreadPool;
import org.example.ThreadPools.ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Пример с фиксированным пулом
//        {
//            ThreadPool fixedThreadPool = new FixedThreadPool(3);
//            for (int i = 0; i < 5; i++) {
//                int finalI = i;
//                fixedThreadPool.execute(() -> {
//                    System.out.println("FixedThreadPool: Task " + finalI + " has been completed in " + Thread.currentThread().getName());
//                });
//            }
//            fixedThreadPool.start();
//        }

//         Пример с расширяемым пулом
        {
            ThreadPool scalableThreadPool = new ScalableThreadPool(2, 15);
            scalableThreadPool.start();

            for (int i = 0; i < 7; i++) {
                final int taskId = i;
                scalableThreadPool.execute(() -> {
                    System.out.println("Executing task " + taskId);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            Thread.sleep(17000);

            for (int i = 0; i < 15; i++) {
                final int taskId = i;
                scalableThreadPool.execute(() -> {
                    System.out.println("Executing task " + taskId);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }
}
