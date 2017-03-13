package com.fibers.core;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;

import java.util.concurrent.Callable;

/**
 * Created by mohan.pandian on 10/03/17.
 */
public class FiberCallable implements Callable<String> {
    private final String message;
    private final long sleep;

    public FiberCallable(String message, long sleep) {
        this.message = message;
        this.sleep = sleep;
    }

    @Override
    @Suspendable
    public String call() throws Exception {
        Strand.sleep(sleep);
        return message;
    }
}
