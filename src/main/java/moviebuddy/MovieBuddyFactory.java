package moviebuddy;

import com.github.benmanes.caffeine.cache.Caffeine;
import moviebuddy.cache.CachingAdvice;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.cache.annotation.CacheResult;
import java.util.concurrent.TimeUnit;

/**
 * @Configuration 어노테이션을 붙임으로,
 * 스프링의 빈 구성정보 => Configuration Metadata로 사용이 될 수 있음을 선언.
 */
@Configuration
@PropertySource("/application.properties")
// 스프링은 자동 클래스 탐지 기법으로 스프링 컨테이너에 빈을 자동으로 등록해 주는 기능을 제공.
// @ComponentScan 을 통해 활성화. -> 지정된 패키지 경로에서 @Component 와 같이 스테레오 타입으로 선언된 클래스를 찾아 빈으로 동록하고 관리.
// 패키지를 지정하지 않으면 @ComponentScan 이 선언된 클래스의 패키지 경로를 기준으로 탐색.
@ComponentScan(basePackages = { "moviebuddy" })
@Import({ MovieBuddyFactory.DomainModuleConfig.class, MovieBuddyFactory.DataSourceModuleConfig.class })
public class MovieBuddyFactory {

    @Bean  // Jaxb2Marshaller : JAXB를 이용해 마샬링,언마샬링을 해주는 스프링의 OXM 모듈 구현체
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // 스프링의 자동 클래스 탐지와 유사하게 지정된 패키지에서 XML을 자바 객체로 변환 시 사용할 클래스를 찾아서 사용을 할 수 있음.
        marshaller.setPackagesToScan("moviebuddy");
        return marshaller;
    }

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS));

        return cacheManager;
    }

    // Advisor = PointCut(대상 선정 알고리즘) + Advice(부가기능)
    @Bean
    public Advisor cachingAdvisor(CacheManager cacheManager) {
        // Pointcut : 부가기능을 적용할 메소드(조인 포인트) 선정 알고리즘을 담은 객체.
        // - NameMatchMethodPointcut : 대상 객체의 메소드 이름이 매치되면 조인 포인트로 선정하는 포인트컷.
//        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
//        pointcut.setMappedName("load*");

        // - AnnotationMatchingPointcut : 대상 객체나 메소드에 특정 어노테이션이 선언되어 있으면 조인 포인트로 선정하는 포인트컷.
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, CacheResult.class);

        Advice advice = new CachingAdvice(cacheManager);

        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /**
     * cf. DefaultAdvisorAutoProxyCreator 는 BeanPostProcessor (빈 후처리기) interface 구현체.
     *  BeanPostProcessor 는 생성된 스프링 빈 객체를 후처리 할 수 있게 해준다.
     *  후처리한다는 것은 강제로 빈의 의존관게 변경 또는 프로퍼티를 통한 새로운 값 설정. 더 나아가 만들어진 빈을 대체할 수도 있다.
     *  --> 자동 프락시 생성기는 빈 후처리기를 통해 스프링 컨테이너가 구성될 때 빈을 프락시로 대채한다.
     * * 종류
     *  - DefaultAdvisorAutoProxyCreator : 일반적으로 사용되는 자동 프락시 생성기로 스프링 컨테이너에 등록된 어드바이저 내의 포인트컷을 통해 프락시 적용 대상을 확인하고, 적용 대상이면 프락시 생성 후 어드바이저를 연결한다.
     *  - BeanNameAutoProxyCreator : 빈의 이름으로 프락시 적용 대상을 판단하고, 이름이 매치되면 프락시 생성 후 내부에 등록된 어드바이스를 연결한다.
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        // 자동 프락시 생성 빈 후처리기는 스프링 컨테이너에 등록된 모든 Advisor를 찾아 프락시 생성시 사용한다
        return new DefaultAdvisorAutoProxyCreator();
    }


    @Configuration
    static class DomainModuleConfig {
    }

    @Configuration
    static class DataSourceModuleConfig {
    }
}
