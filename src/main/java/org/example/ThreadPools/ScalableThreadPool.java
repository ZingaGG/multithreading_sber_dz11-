package org.example.ThreadPools;

import java.util.LinkedList;
import java.util.Queue;

public class ScalableThreadPool implements ThreadPool {
    private final int minCap;
    private final int maxCap;
    private final Queue<Runnable> taskQueue = new LinkedList<>();
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
        taskQueue.add(runnable);
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
        return taskQueue.poll();
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

