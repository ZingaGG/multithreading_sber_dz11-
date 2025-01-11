package org.example.ThreadPools;
import java.util.LinkedList;
import java.util.Queue;

public class FixedThreadPool implements ThreadPool {
    private final int poolCap;
    private final Queue<Runnable> taskQueue = new LinkedList<>();
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
        taskQueue.add(runnable);
        notify();
    }

    private synchronized Runnable takeTask() throws InterruptedException {
        while (taskQueue.isEmpty()) {
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
                    System.out.println("FixedThreadPool: Thread " + Thread.currentThread().getName() + " has taken the task.");
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

