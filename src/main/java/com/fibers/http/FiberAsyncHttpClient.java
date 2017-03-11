package com.fibers.http;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.httpasyncclient.FiberCloseableHttpAsyncClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by mohan.pandian on 03/03/17.
 */
public class FiberAsyncHttpClient {
    private final CloseableHttpAsyncClient client;

    public FiberAsyncHttpClient() {
        client = FiberCloseableHttpAsyncClient.wrap(HttpAsyncClients.
                custom().
                setMaxConnPerRoute(10).
                setMaxConnTotal(10).
                build());
        client.start();
    }

    @Suspendable
    public String executeGet(String uri) throws ExecutionException, InterruptedException, IOException {
        System.out.println(Thread.currentThread().getName());
        return EntityUtils.toString(client.execute(new HttpGet(uri), null).get().getEntity());
    }

    @Suspendable
    public void close() throws IOException {
        client.close();
    }
}
