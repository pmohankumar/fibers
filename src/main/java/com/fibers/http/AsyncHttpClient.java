package com.fibers.http;

import org.apache.http.*;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by mohan.pandian on 16/05/17.
 */
public class AsyncHttpClient {
    private CloseableHttpAsyncClient client;
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String COMPRESSION_TYPE = "gzip";

    public AsyncHttpClient() throws IOReactorException {
        RegistryBuilder<SchemeIOSessionStrategy> registryBuilder = RegistryBuilder.create();
        registryBuilder.register("https", SSLIOSessionStrategy.getDefaultStrategy()).register("http", NoopIOSessionStrategy.INSTANCE);

        IOReactorConfig reactorConfig = IOReactorConfig.custom().
                setConnectTimeout(5000).
                setSoTimeout(2000).
                setIoThreadCount(1).
                build();

        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(reactorConfig);
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor, null, registryBuilder.build(), null, null, 60, TimeUnit.SECONDS);
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(100);
        cm.setMaxPerRoute(new HttpRoute(new HttpHost("jsonplaceholder.typicode.com", 80)), 100);

//        this.client = FiberHttpClientBuilder
//                .create(Runtime.getRuntime().availableProcessors())
//                .setMaxConnPerRoute(maxConnections)
//                .setMaxConnTotal(maxConnections)
//                .build();

        HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClientBuilder.create().
                setMaxConnPerRoute(100).
                setMaxConnTotal(100).
                disableCookieManagement().
                setConnectionManager(cm);

        httpAsyncClientBuilder.addInterceptorLast((HttpRequest httpRequest, HttpContext httpContext) -> {
            if (!httpRequest.containsHeader(ACCEPT_ENCODING)) {
                httpRequest.addHeader(ACCEPT_ENCODING, COMPRESSION_TYPE);
            }
        });
        httpAsyncClientBuilder.addInterceptorFirst((HttpResponse httpResponse, HttpContext httpContext) -> {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase(COMPRESSION_TYPE)) {
                            httpResponse.setEntity(new GzipDecompressingEntity(httpResponse.getEntity()));
                            return;
                        }
                    }
                }
            }
        });

        this.client = httpAsyncClientBuilder.build();
        this.client.start();
    }

    public String executeGet(String uri) throws ExecutionException, InterruptedException, IOException {
        System.out.println(Thread.currentThread().getName());
        return EntityUtils.toString(new GzipDecompressingEntity(client.execute(new HttpGet(uri), null).get().getEntity()));
    }

    public void close() throws IOException {
        client.close();
    }
}
