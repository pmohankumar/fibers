package com.fibers.core;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by mohan.pandian on 02/03/17.
 */
public class FiberTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Fiber fiber1 = getFiber(5000, "Hi slept for 5s").start();
        Fiber fiber2 = getFiber(4000, "Hi slept for 4s").start();
        System.out.println(fiber2.get(5000, TimeUnit.MILLISECONDS));
        System.out.println(fiber1.get(6000, TimeUnit.MILLISECONDS));
    }

    private static Fiber getFiber(final long sleep, final String message) {
        return new Fiber<String>() {
            @Override
            protected String run() throws SuspendExecution, InterruptedException {
                Strand.sleep(sleep);
                return message;
            }
        };
    }

}
