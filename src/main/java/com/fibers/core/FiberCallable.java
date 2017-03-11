package com.fibers.core;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;

import java.util.concurrent.Callable;

/**
 * Created by mohan.pandian on 10/03/17.
 */
public class FiberCallable implements Callable<String> {
    @Override
    @Suspendable
    public String call() throws Exception {
        Strand.sleep(2000);
        return "Hello World";
    }
}
