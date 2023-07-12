package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {

    @Test
    void dynamicA() {
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        /*
        프록시 객체를 만들 클래스 로더 지정,
        프록시 기반 인터페이스 지정,
        프록시 공통 기본 로직을 알고 있는 handler 지정
         */
        AInterface proxy = (AInterface) Proxy.newProxyInstance(
                AInterface.class.getClassLoader(),
                new Class[]{AInterface.class},
                handler
        );

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

    @Test
    void dynamicB() throws NoSuchMethodException {
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        /*
        프록시 객체를 만들 클래스 로더 지정,
        프록시 기반 인터페이스 지정,
        프록시 공통 기본 로직을 알고 있는 handler 지정
         */
        BInterface proxy = (BInterface) Proxy.newProxyInstance(
                BInterface.class.getClassLoader(),
                new Class[]{BInterface.class},
                handler
        );

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        for (Method method : proxy.getClass().getMethods()) {
            //jdk프록시에 의해 구현된 클래스의 타입은 지정된 인터페이스이고
            //인터페이스의 메서드 + Proxy를 위한 실행처리기 호출 메서드등도 가지고 있다
            log.info("메서드 이름={}", method.getName());
        }
    }
}
