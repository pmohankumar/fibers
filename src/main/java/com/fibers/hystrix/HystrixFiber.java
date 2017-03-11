package com.fibers.hystrix;

import com.netflix.hystrix.*;

/**
 * Created by mohan.pandian on 06/03/17.
 */
public class HystrixFiber extends HystrixCommand<String> {
    protected HystrixFiber() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HystrixFiberGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("HystrixFiberCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HystrixFiberThreadPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withAllowMaximumSizeToDivergeFromCoreSize(true).withCoreSize(2).withMaximumSize(4))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(2000)));
    }

    @Override
    protected String run() throws Exception {
        System.out.println(Thread.currentThread().getName());
        return "Hello World!";
    }
}
