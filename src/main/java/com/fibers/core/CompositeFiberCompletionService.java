package com.fibers.core;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by mohan.pandian on 15/03/17.
 */
public class CompositeFiberCompletionService<T> {
    private final Channel<Future<T>> channel = Channels.newChannel(-1);

    @Suspendable
    private Future<T> submit(Callable<T> callable) {
        return new Fiber<T>() {
            @Override
            protected T run() throws SuspendExecution, InterruptedException {
                try {
                    return callable.call();
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                } finally {
                    channel.send(this);
                }
            }
        }.start();
    }

    @Suspendable
    public List<Future<T>> submit(List<Callable<T>> callables) {
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> callable : callables) {
            futures.add(submit(callable));
        }
        return futures;
    }

    @Suspendable
    public void wait(int totalCount, long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalCount; i++) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            long remainingTime = timeout - elapsedTime;
            if (remainingTime > 0) {
                try {
                    Future<T> future = channel.receive(remainingTime, TimeUnit.MILLISECONDS);
                    if (future == null) {
                        throw new TimeoutException();
                    }
                    future.get();
                } catch (SuspendExecution suspendExecution) {
                    suspendExecution.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
        channel.close();
    }
}
