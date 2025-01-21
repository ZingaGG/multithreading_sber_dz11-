package org.example;

import org.example.ThreadPools.FixedThreadPool;
import org.example.ThreadPools.ScalableThreadPool;
import org.example.ThreadPools.ThreadPool;

public class Main {
    public static void main(String[] args) {
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

        // Пример с расширяемым пулом
        {
            ThreadPool scalableThreadPool = new ScalableThreadPool(2, 15);
            for (int i = 0; i < 30; i++) { // можно менять i для наглядности, чтобы увидеть сужение и расширения пула
                int finalI = i;
                scalableThreadPool.execute(() -> {
                    System.out.println("ScalableThreadPool: Task " + finalI + " has been completed in " + Thread.currentThread().getName());
                });
            }
            scalableThreadPool.start();

        }
    }
}
