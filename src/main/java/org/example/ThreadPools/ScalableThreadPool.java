package org.example.ThreadPools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ScalableThreadPool implements ThreadPool {
    private final int minCap;
    private final int maxCap;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final CopyOnWriteArrayList<HelpThread> threads = new CopyOnWriteArrayList<>(); // Синхронизованная версия

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
        if (!taskQueue.offer(runnable)) { // Метод put будет блокировать поток вызывающий метод, если очередь переполнена, а offer просто вернет false и поток будет не заблокирован -> меньше простоя, быстрее программа
            System.out.println("Task queue is full, unable to add task.");
            return;
        }

        int allTasksInWork = taskQueue.size() + threads.size(); // Если я правильно понял, то вместо сайза заданий в очереди, мы считаем их + задания в работе
        if (allTasksInWork > threads.size() && threads.size() < maxCap) {
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
            wait();
        }
        return taskQueue.poll(); // take -> poll
    }

    private class HelpThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable task = takeTask();
                    if (task == null) {
                        break;
                    }
                    System.out.println("ScalableThreadPool: Thread " + Thread.currentThread().getName() + " has taken the task.");
                    System.out.println("Quantity of threads at the moment - " + threads.size());
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
