package org.example.ThreadPools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ScalableThreadPool implements ThreadPool {
    private final int minCap;
    private final int maxCap;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private int threadsQuantity;

    public ScalableThreadPool(int minThreads, int maxThreads) {
        this.minCap = minThreads;
        this.maxCap = maxThreads;
        threadsQuantity = minThreads;
    }

    @Override
    public void start() {
        for (int i = 0; i < minCap; i++) {
            new HelpThread().start();
        }
    }

    @Override
    public synchronized void execute(Runnable runnable) {
        try {
            taskQueue.put(runnable);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());;
        }
        if (taskQueue.size() > threadsQuantity && threadsQuantity < maxCap) {
            threadsQuantity++;
            new HelpThread().start();
        }
        notify();
    }

    private synchronized Runnable takeTask() throws InterruptedException {
        while (taskQueue.isEmpty()) {
            if (threadsQuantity > minCap) {
                threadsQuantity--;
                return null;
            }
            wait();
        }
        return taskQueue.take();
    }

    private class HelpThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = takeTask();
                    if (task != null) {
                        System.out.println("ScalableThreadPool: Thread " + Thread.currentThread().getName() + " has taken the task.");
                        System.out.println("Quantity of threads in the moment - " + threadsQuantity);
                        task.run();
                    } else {
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

