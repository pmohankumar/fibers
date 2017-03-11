package com.fibers.http;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by mohan.pandian on 07/03/17.
 */
public class FiberHttpClient {
    private final CloseableHttpClient client;

    public FiberHttpClient() {
        this.client = FiberHttpClientBuilder.
                create(2). // use 2 io threads
                setMaxConnPerRoute(10).
                setMaxConnTotal(10).
                build();
    }

    @Suspendable
    public String executeGet(String uri) throws ExecutionException, InterruptedException, IOException {
        System.out.println(Thread.currentThread().getName());
        return client.execute(new HttpGet(uri), new BasicResponseHandler());
    }

    @Suspendable
    public void close() throws IOException {
        client.close();
    }
}
