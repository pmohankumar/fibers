package com.fibers.core;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import com.fibers.hystrix.FiberThreadPoolExecutor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * Created by mohan.pandian on 10/03/17.
 */
public class FiberCallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println(getFiber().start().get());
    }

    private static Fiber getFiber() {
        return new Fiber<String>() {
            @Override
            protected String run() throws SuspendExecution, InterruptedException {
                ExecutorCompletionService executorCompletionService = new FiberExecutorCompletionService(new FiberThreadPoolExecutor());
                Future<String> future = executorCompletionService.submit(new FiberCallable());
                try {
                    return future.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}
