package moviebuddy;

import com.github.benmanes.caffeine.cache.Caffeine;
import moviebuddy.cache.CachingAdvice;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

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
        // Pointcut : 특정 조건에 의해 필터링된 조인포인트, 수많은 조인포인트 중에 특정 메소드에서만 횡단 공통기능을 수행시키기 위해서 사용
        // NameMatchMethodPointcut -> 메소드 이름 패턴을 이용해 조인 포인트를 선정하는 포인트컷을 작성
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("load*");

        Advice advice = new CachingAdvice(cacheManager);

        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /**
     * cf. DefaultAdvisorAutoProxyCreator 는 BeanPostProcessor (빈 후처리기) interface 구현체.
     *  BeanPostProcessor 는 생성된 스프링 빈 객체를 후처리 할 수 있게 해준다.
     *  후처리한다는 것은 강제로 빈의 의존관게 변경 또는 프로퍼티를 통한 새로운 값 설정. 더 나아가 만들어진 빈을 대체할 수도 있다.
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

        /**
         * ProxyFactoryBean 의 한계
         * - 부가 기능을 적용하기 위해 프락시를 생성하는 부분 -> 프락시를 빈으로 동록하는 코드를 필요한 만큼 수십,수백개 작성해야한다.
         * - 어드바이스가 대상 객체가 가진 toString(), etClass() 등 모든 메서드에 부가 기능을 부여한다.
         *
         * @param applicationContext
         * @return
         */
//        @Primary
//        @Bean
//        public ProxyFactoryBean cachingMovieReaderFactory(ApplicationContext applicationContext){
//            MovieReader target = applicationContext.getBean(MovieReader.class);
//            CacheManager cacheManager = applicationContext.getBean(CacheManager.class);
//
//            // 자바에는 JDK에서 제공하는 동적 프락시 외에도 프락시를 다룰 수 있는 다양한 기술이 존재. (ex. CGLIB, Javassist, Byte Buddy)
//            // 스프링의 AOP 모듈은 이런 다양한 프락시 기술들을 일관된 방식으로 사용할 수 있도록 ProxyFactoryBean 이라는 서비스 추상화를 제공.
//            // ProxyFactoryBean은 프락시를 생성해서 빈 객체를 등록해 주는 팩토리 빈.
//            // JDK 동적 프락시와는 달리, 프락시 팩토리 빈은 순수하게 프락시를 생성하는 작업만 담당하고, 프락시를 통해 제공할 부가 기능은 별도 객체로 생성해 스프링 컨테이너에 빈으로 등록해 줄 수 있다.
//            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
//            proxyFactoryBean.setTarget(target);
//            // 클래스 프락시 활성화(true)/비활성화(false, 기본값)
//            //proxyFactoryBean.setProxyTargetClass(true);
//            proxyFactoryBean.addAdvice(new CachingAdvice(cacheManager));
//
//            return proxyFactoryBean;
//        }
    }
}
