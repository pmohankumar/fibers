package com.fibers.core;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by mohan.pandian on 12/03/17.
 */
public class FiberExecutorService<T> {
    private final Channel<Future<T>> channel = Channels.newChannel(-1);

    @Suspendable
    public Future<T> submit(Callable<T> callable) {
        return new Fiber<T>() {
            @Override
            protected T run() throws SuspendExecution, InterruptedException {
                try {
                    return callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    channel.send(this);
                }
                return null;
            }
        }.start();
    }

    @Suspendable
    public Future<T> take() throws InterruptedException, SuspendExecution {
        return channel.receive();
    }
}
