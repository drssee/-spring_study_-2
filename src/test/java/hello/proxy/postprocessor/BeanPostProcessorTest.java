package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

public class BeanPostProcessorTest {

    @Test
    void basicConfig() {
        //수동으로 스프링 컨테이너 생성 + @Configuration 클래스 등록
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);

        //빈후처리기를 통해 beanA의 값이 B객체로 바꿔치기된다
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();

        Assertions.assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean(A.class)
        );
    }

    @Slf4j
    static class A {
        @PostConstruct
        private void postConstructCall() {
            log.info("호출됐나?");
        }

        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }

    @Configuration
    static class BeanPostProcessorConfig {
        @Bean(name="beanA")
        public A a() {
            return new A();
        }

        @Bean
        public BeanPostProcessor beanPostProcessor() {
            return new AToBPostProcessor();
        }
    }

    @Slf4j
    static class AToBPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName={}, bean={}", beanName, bean);
            if (bean instanceof A) {
                return new B();
            }
            return bean;
        }
    }
}
