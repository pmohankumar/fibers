package com.fibers.hystrix;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;

import java.util.concurrent.*;

/**
 * Created by mohan.pandian on 06/03/17.
 */
public class FiberThreadPoolExecutor extends ThreadPoolExecutor {
    public FiberThreadPoolExecutor() {
        this(20, 20, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(20));
    }

    public FiberThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public FiberThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public FiberThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public FiberThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    @Suspendable
    public void execute(Runnable command) {
        new Fiber<Void>() {
            @Override
            protected Void run() throws SuspendExecution, InterruptedException {
                command.run();
                return null;
            }
        }.start();
    }
}
