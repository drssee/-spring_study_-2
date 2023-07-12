package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * JdkProxy의 InvocationHandler,
 * Cglib의 MethodInterceptor
 * 스프링이 Advice <- MethodInterceptor(스프링aop)로 추상화를 함
 * 이로 인해 ProxyFactor는 Advice에만 의존하면 구현기술이 JdkProxy든 Cglib든 관계없이
 * 모든 경우의 수에 스프링 ProxyFactory를 적용할 수 있다
 */
@Slf4j
public class TimeAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        //프록시팩토리에 저장된 타겟의 메서드를 호출한다
        Object result = invocation.proceed();

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}",resultTime);
        return result;
    }
}
