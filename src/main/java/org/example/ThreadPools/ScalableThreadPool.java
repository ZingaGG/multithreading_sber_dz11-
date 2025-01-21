package org.example.ThreadPools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ScalableThreadPool implements ThreadPool {
    private final int minCap;
    private final int maxCap;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final List<HelpThread> threads = new ArrayList<>();

    public ScalableThreadPool(int minThreads, int maxThreads) {
        this.minCap = minThreads;
        this.maxCap = maxThreads;
    }

    @Override
    public void start() {
        for (int i = 0; i < minCap; i++) {
            HelpThread helpThread = new HelpThread();
            threads.add(helpThread);
            helpThread.start();
        }
    }

    @Override
    public synchronized void execute(Runnable runnable) {
        try {
            taskQueue.put(runnable);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        if (taskQueue.size() - 1 > threads.size() && threads.size() < maxCap) {
            HelpThread helpThread = new HelpThread();
            threads.add(helpThread);
            helpThread.start();
        }
        notifyAll();
    }

    private synchronized Runnable takeTask() throws InterruptedException {
        while (taskQueue.isEmpty()) {
            if (threads.size() > minCap) {
                HelpThread threadToInterrupt = threads.remove(threads.size() - 1);
                threadToInterrupt.interrupt();
                return null;
            }
            return null;
        }
        return taskQueue.take();
    }

    private class HelpThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable task = takeTask();
                    if(task == null){
                        break;
                    }
                    System.out.println("ScalableThreadPool: Thread " + Thread.currentThread().getName() + " has taken the task.");
                    System.out.println("Quantity of threads in the moment - " + threads.size());
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
