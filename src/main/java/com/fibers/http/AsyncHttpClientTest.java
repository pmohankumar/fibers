package com.fibers.http;

/**
 * Created by mohan.pandian on 16/05/17.
 */
public class AsyncHttpClientTest {
    public static void main(String[] args) throws Exception {
        AsyncHttpClient client = null;
        try {
            client = new AsyncHttpClient();
            System.out.println(client.executeGet("http://jsonplaceholder.typicode.com/users?id=1"));
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
