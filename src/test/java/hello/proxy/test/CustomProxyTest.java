package hello.proxy.test;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프록시팩토리, 빈후처리기를 이용하여 프록시객체를 스프링컨테이너에 등록하기
 * 1.기존의 프록시팩토리 프록시 생성 코드와 동일하게,
 *  Advisor(Pointcut + Advice)작성 + 적용할 Target 작성 + Advisor와 Target을 이용하여 프록시팩토리 생성
 * 2.생성된 프록시팩토리를 이용하여 프록시객체를 얻는것은 똑같으나,
 *  빈후처리기에 프록시 등록코드를 작성하여, 중복코드를 제거하고 프록시 생성 및 관리를 한 코드에서 할수 있는 이점이 있다
 *  그리고 @Component 어노테이션에 의해 자동으로 스프링컨테이너에 등록되는 빈들도, 빈후처리기를 이용하여 조작할 수 있다는 장점이 있다
 */
@Slf4j
public class CustomProxyTest {

    /*
        target 생성
     */
    static class CustomTarget {
        public void call() {
            log.info("원본target call 호출됨");
        }
    }

    /*
        advice 생성
     */
    static class CustomAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("프록시 advice 실행={}", invocation.getMethod().getName());
            Object result = invocation.proceed();
            log.info("프록시 advice 종료={}", result);
            return result;
        }
    }

    /*
        프록시객체 등록을 위한 빈 후처리기 생성
     */
    static class PostBean implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("빈 후처리기 진입 beanName={}", beanName);
            if (!bean.getClass().getPackageName().startsWith("hello.proxy.test")) {
                return bean;
            }

            //타겟 넘겨주며 프록시팩토리 생성
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            //어드바이저 등록
            proxyFactory.addAdvisor(getAdvisor());
            //프록시 생성
            Object proxy = proxyFactory.getProxy();
            log.info("proxy={}", proxy.getClass());
            //스프링컨테이너에 등록될 빈을 프록시로 교체
            return proxy;
        }

        /*
            advisor 생성 메서드
         */
        private DefaultPointcutAdvisor getAdvisor() {
            //포인트컷과 어드바이스 생성
            NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
            pointcut.setMappedName("call*");
            CustomAdvice advice = new CustomAdvice();

            //어드바이스 리턴
            return new DefaultPointcutAdvisor(pointcut, advice);
        }
    }

    @Configuration
    static class CustomProxyConfiguration {
        @Bean
        public BeanPostProcessor beanPostProcessor() {
            return new PostBean();
        }

        @Bean
        public CustomTarget customTarget() {
            return new CustomTarget();
        }
    }

    @Test
    void customProxy() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(CustomProxyConfiguration.class);
        CustomTarget customTarget = applicationContext.getBean(CustomTarget.class);
        customTarget.call();
    }
}
