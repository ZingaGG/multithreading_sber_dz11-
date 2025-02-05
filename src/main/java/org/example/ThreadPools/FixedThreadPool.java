package org.example.ThreadPools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool implements ThreadPool {
    private final int poolCap;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final HelpThread[] threads;

    public FixedThreadPool(int poolSize) {
        this.poolCap = poolSize;
        threads = new HelpThread[poolSize];
    }

    @Override
    public void start() {
        for (int i = 0; i < poolCap; i++) {
            threads[i] = new HelpThread();
            threads[i].start();
        }
    }

    @Override
    public synchronized void execute(Runnable runnable) {
        try {
            taskQueue.put(runnable);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());;
        }
        notify();
    }

    private synchronized Runnable takeTask() throws InterruptedException {
        return taskQueue.take();
    }

    private class HelpThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = takeTask();
                    System.out.println("FixedThreadPool: Thread " + Thread.currentThread().getName() + " has taken the task.");
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

