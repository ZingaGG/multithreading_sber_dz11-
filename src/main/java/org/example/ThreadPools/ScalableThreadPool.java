package org.example.ThreadPools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScalableThreadPool implements ThreadPool {
    private final int minCap;
    private final int maxCap;
    private static final long idleTimeout = 5000;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final CopyOnWriteArrayList<HelpThread> threads = new CopyOnWriteArrayList<>();
    private final AtomicInteger activeTasks = new AtomicInteger(0);

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
        if (!taskQueue.offer(runnable)) {
            System.out.println("Task queue is full, unable to add task.");
            return;
        }

        int allTasksInWork = activeTasks.get() + taskQueue.size();
        if (allTasksInWork > threads.size() && threads.size() < maxCap) {
            HelpThread helpThread = new HelpThread();
            threads.add(helpThread);
            helpThread.start();
        }
    }

    private class HelpThread extends Thread {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Runnable task = null;
                try {
                    task = taskQueue.poll(idleTimeout, TimeUnit.MILLISECONDS);

                    if (task == null) {
                        if (threads.size() > minCap) {
                            shutdownThread(this);
                            break;
                        }
                    } else {
                        activeTasks.incrementAndGet();
                        System.out.println("ScalableThreadPool: Thread " + Thread.currentThread().getName() + " has taken the task.");
                        System.out.println("Quantity of threads at the moment - " + threads.size());
                        task.run();
                        activeTasks.decrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private void shutdownThread(HelpThread thread) {
            thread.interrupt();
            threads.remove(thread);
            System.out.println("Thread " + thread.getName() + " has been shut down. Remaining threads: " + threads.size());
        }
    }
}