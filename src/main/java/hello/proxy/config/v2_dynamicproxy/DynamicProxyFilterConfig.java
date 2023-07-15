package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceBasicHandler;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceFilterHandler;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

//@Configuration
public class DynamicProxyFilterConfig {

    private final LogTrace logTrace;
    private static final String[] PATTERNS = {"request*", "order*", "save*"};

    public DynamicProxyFilterConfig() {
        this.logTrace = new ThreadLocalLogTrace();
    }

    @Bean
    public OrderControllerV1 orderControllerV1() {
        OrderControllerV1 target = new OrderControllerV1Impl(orderServiceV1());
        //생성될 프록시의 클래스로더, 생성될 프록시의 기반 인터페이스, 생성될 프록시의 오버라이딩 구현 코드
        return (OrderControllerV1) Proxy.newProxyInstance(
                OrderControllerV1.class.getClassLoader(),
                new Class[]{OrderControllerV1.class},
                new LogTraceFilterHandler(target, logTrace, PATTERNS)
        );
    }

    @Bean
    public OrderServiceV1 orderServiceV1() {
        OrderServiceV1 target = new OrderServiceV1Impl(orderRepositoryV1());
        return (OrderServiceV1) Proxy.newProxyInstance(
                OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class},
                new LogTraceFilterHandler(target, logTrace, PATTERNS)
        );
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1() {
        OrderRepositoryV1 target = new OrderRepositoryV1Impl();
        return (OrderRepositoryV1) Proxy.newProxyInstance(
                OrderRepositoryV1.class.getClassLoader(),
                new Class[]{OrderRepositoryV1.class},
                new LogTraceFilterHandler(target, logTrace, PATTERNS)
        );
    }
}
