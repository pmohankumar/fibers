package com.fibers.http;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by mohan.pandian on 07/03/17.
 */
public class FiberHttpClientTest {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, TimeoutException, SuspendExecution {
        test();
    }

    private static void test() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        final FiberHttpClient fiberHttpClient = new FiberHttpClient();
        Fiber fiber = null;
        try {
            fiber = new Fiber<String>() {
                @Override
                protected String run() throws SuspendExecution, InterruptedException {
                    String response = null;
                    try {
                        System.out.println(Thread.currentThread().getName());
                        System.out.println("Executing GET");
                        response = fiberHttpClient.executeGet("http://jsonplaceholder.typicode.com/users?id=1");
                        System.out.println("Executed GET");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            }.start();
            System.out.println(fiber.get(1000, TimeUnit.MILLISECONDS));
//            System.out.println(fiberHttpClient.executeGet("http://jsonplaceholder.typicode.com/users?id=1"));
        } finally {
//            fiber.interrupt();
            fiberHttpClient.close();
        }
    }
}
