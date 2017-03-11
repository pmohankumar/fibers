package com.fibers.core;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

import java.util.concurrent.*;

/**
 * Created by mohan.pandian on 10/03/17.
 */
public class FiberExecutorCompletionService<V> extends ExecutorCompletionService<V> {
    private final Executor executor;
    private final Channel<Future<V>> channel;

    /**
     * FutureTask extension to enqueue upon completion
     */
    private class QueueingFuture extends FutureTask<Void> {
        QueueingFuture(RunnableFuture<V> task) {
            super(task, null);
            this.task = task;
        }

        protected void done() {
            try {
                channel.send(task);
            } catch (SuspendExecution suspendExecution) {
                suspendExecution.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private final Future<V> task;
    }

    private RunnableFuture<V> newTaskFor(Callable<V> task) {
        return new FutureTask<V>(task);
    }

    private RunnableFuture<V> newTaskFor(Runnable task, V result) {
        return new FutureTask<V>(task, result);
    }

    /**
     * Creates an ExecutorCompletionService using the supplied
     * executor for base task execution and a
     * {@link LinkedBlockingQueue} as a completion queue.
     *
     * @param executor the executor to use
     * @throws NullPointerException if executor is {@code null}
     */
    public FiberExecutorCompletionService(Executor executor) {
        super(executor);
        if (executor == null)
            throw new NullPointerException();
        this.executor = executor;
        this.channel = Channels.newChannel(-1);
    }

    /**
     * Creates an ExecutorCompletionService using the supplied
     * executor for base task execution and the supplied queue as its
     * completion queue.
     *
     * @param executor        the executor to use
     * @param channel the queue to use as the completion queue
     *                        normally one dedicated for use by this service. This
     *                        queue is treated as unbounded -- failed attempted
     *                        {@code Queue.add} operations for completed tasks cause
     *                        them not to be retrievable.
     * @throws NullPointerException if executor or channel are {@code null}
     */
    public FiberExecutorCompletionService(AbstractExecutorService executor,
                                          BlockingQueue<Future<V>> channel) {
        super(executor);
        if (executor == null || channel == null)
            throw new NullPointerException();
        this.executor = executor;
        this.channel = Channels.newChannel(-1);
    }

    public Future<V> submit(Callable<V> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task);
        executor.execute(new QueueingFuture(f));
        return f;
    }

    public Future<V> submit(Runnable task, V result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task, result);
        executor.execute(new QueueingFuture(f));
        return f;
    }

    @Suspendable
    public Future<V> take() throws InterruptedException {
        try {
            return channel.receive();
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
            return null;
        }
    }

    public Future<V> poll() {
        return null;
    }

    public Future<V> poll(long timeout, TimeUnit unit)
            throws InterruptedException {
        return null;
    }
}
