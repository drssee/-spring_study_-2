package hello.proxy.config.v3_proxyfactory.advice;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LogTraceAdvice implements MethodInterceptor {

    private final LogTrace logTrace;

    public LogTraceAdvice(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TraceStatus status = null;
        try {
            String message = invocation.getMethod().getDeclaringClass().getSimpleName() + "." + invocation.getMethod().getName() + "()";
            status = logTrace.begin(message);
            //로직 호출
            /*
                (주의)jdk의 invocationHandler cglib의 methodInterceptor를 추상화한 스프링의 어드바이스는
                어드바이스 내부코드에서 각각 jdk, cglib의 인터셉터 방식을 호출하는식으로 적용된다
                (주의)프록시객체의 invocation.proceed()는 프록시어드바이스큐(임시가칭)에 등록된 순서대로
                예를들어 프록시 - 어드바이스1,어드바이스2,타겟 순서대로 등록되어 있다고 했을시
                각기 다음 체이닝을 소환하는 역할을 한다, 즉 invocation.proceed()를 한다고 해서
                원본 타겟이 중복호출되는것이 아니라 타겟 전에 적용할 어드바이스가 있을경우(포인트컷으로 판단) 그 어드바이스 코드를 호출하는 역할을 한다
             */
            Object result = invocation.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
