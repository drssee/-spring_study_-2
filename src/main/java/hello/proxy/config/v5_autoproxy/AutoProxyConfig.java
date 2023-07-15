package hello.proxy.config.v5_autoproxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice2;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {

    /*
        v4와 달리 어드바이저만 빈으로 등록하면 되는 이유
        스프링aop 라이브러리 설치시, 스프링은 자동으로 빈후처리기를 스프링 컨테이너에 빈으로 등록한다
        등록된 빈후처기리는 스프링컨테이너에 빈으로 등록된 모든 어드바이저를 찾아서,
        어드바이저 빈이 등록되어 있다면 포인트컷을 판단하여 해당 될경우 프록시 객체를 만들어 스프링 빈으로 대신 등록한다
        등록된 프록시 객체는, 호출될시 호출된 메서드를 포인트컷을 이용해 어드바이스 적용 여부를 판단한다

        (주의)포인트컷은 빈후처리기에서 프록시객체 생성여부를 판단할때 1번,
        프록시객체 호출시 해당 메서드에 어드바이스 코드를 적용할지 판단할때 2번 사용된다
     */
    @Bean
    public Advisor advisor1(LogTrace logTrace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        //advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
