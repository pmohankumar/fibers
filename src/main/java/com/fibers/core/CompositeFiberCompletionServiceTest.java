package com.fibers.core;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by mohan.pandian on 15/03/17.
 */
public class CompositeFiberCompletionServiceTest {
    public static void main(String[] args) {
        CompositeFiberCompletionService<String> requiredCompletionService = new CompositeFiberCompletionService<>();
        CompositeFiberCompletionService<String> optionalCompletionService = new CompositeFiberCompletionService<>();

        List<Future<String>> requiredFutures = submitRequired(requiredCompletionService);
        List<Future<String>> optionalFutures = submitOptional(optionalCompletionService);

        long TIMEOUT = 5000;
        long START_TIME = System.currentTimeMillis();
        long elapsedTime = System.currentTimeMillis() - START_TIME;
        long remainingTime = TIMEOUT - elapsedTime;
        wait(requiredCompletionService, true, requiredFutures, remainingTime);

        elapsedTime = System.currentTimeMillis() - START_TIME;
        remainingTime = TIMEOUT - elapsedTime;
        wait(optionalCompletionService, false, optionalFutures, remainingTime);

        elapsedTime = System.currentTimeMillis() - START_TIME;
        remainingTime = TIMEOUT - elapsedTime;
        System.out.println("Done with remainingTime: " + remainingTime);

        requiredCompletionService.shutdown();
        optionalCompletionService.shutdown();
    }

    private static List<Future<String>> submitRequired(CompositeFiberCompletionService completionService) {
        return completionService.submit(Arrays.asList(
                new FiberCallable("Hello World 1", 1000),
                new FiberCallable("Hello World 2", 2000),
                new FiberCallable("Hello World 3", 3000)
        ));
    }

    private static List<Future<String>> submitOptional(CompositeFiberCompletionService completionService) {
        return completionService.submit(Arrays.asList(
                new FiberCallable("Hello World 4", 4000),
                new FiberCallable("Hello World 43", 4300),
                new FiberCallable("Hello World 46", 4600)
        ));
    }

    private static void wait(CompositeFiberCompletionService completionService, boolean required, List<Future<String>> futures, long remainingTime) {
        String requiredOrOptional = required ? "required" : "optional";
        System.out.println("Waiting for " + requiredOrOptional + ": " + remainingTime);
        try {
            completionService.wait(futures.size(), remainingTime);
        } catch (TimeoutException e) {
            System.out.println("Timeout exception in " + requiredOrOptional);
            cancel(futures);
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception in " + requiredOrOptional);
            cancel(futures);
        } catch (ExecutionException e) {
            System.out.println("Execution exception in " + requiredOrOptional);
            cancel(futures);
        }

        print(futures);
    }

    private static void print(List<Future<String>> futures) {
        for (Future<String> future : futures) {
            try {
                System.out.println(future.get());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void cancel(List<Future<String>> futures) {
        for (Future future : futures) {
            if (!future.isCancelled() && !future.isDone()) {
                future.cancel(true);
            }
        }
    }
}
