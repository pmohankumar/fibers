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
    private final Channel<Future<T>> requiredChannel = Channels.newChannel(-1);
    private final Channel<Future<T>> optionalChannel = Channels.newChannel(-1);

    @Suspendable
    private Future<T> submit(Callable<T> callable, boolean required) {
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
                    getChannel(required).send(this);
                }
            }
        }.start();
    }

    @Suspendable
    public List<Future<T>> submit(List<Callable<T>> callables, boolean required) {
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> callable : callables) {
            futures.add(submit(callable, required));
        }
        return futures;
    }

    @Suspendable
    public void wait(int totalCount, long timeout, boolean required) throws TimeoutException, InterruptedException, ExecutionException {
        Channel<Future<T>> channel = getChannel(required);
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

    private Channel<Future<T>> getChannel(boolean required) {
        return required ? requiredChannel : optionalChannel;
    }

    public void shutdown() {
        requiredChannel.close();
        optionalChannel.close();
    }
}
