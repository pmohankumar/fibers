package com.fibers.hystrix;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.netflix.hystrix.strategy.HystrixPlugins;

import java.util.concurrent.ExecutionException;

/**
 * Created by mohan.pandian on 06/03/17.
 */
public class HystrixFiberTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new HystrixFiberConcurrencyStrategy());
        testInFiber();
        testInFiber();
        testInFiber();
    }

    private static void testInFiber() throws ExecutionException, InterruptedException {
        final HystrixFiber hystrixFiber = new HystrixFiber();
//        System.out.println(hystrixFiber.execute());
        Fiber fiber = new Fiber<String>() {
            @Override
            @Suspendable
            protected String run() throws SuspendExecution, InterruptedException {
                System.out.println(Thread.currentThread().getName());
                return hystrixFiber.execute();
            }
        }.start();
        System.out.println(fiber.get());
    }
}
