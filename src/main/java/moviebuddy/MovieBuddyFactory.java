package moviebuddy;

import com.github.benmanes.caffeine.cache.Caffeine;
import moviebuddy.cache.CachingAdvice;
import moviebuddy.domain.MovieReader;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationContext;
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

    @Configuration
    static class DomainModuleConfig {

    }

    @Configuration
    static class DataSourceModuleConfig {
        // cf. 의존 관계 주입 시 2개 이상의 동일한 타입의 빈이 존재할 때, 이 @Primary 어노테이션이 붙은 빈을 우선 사용.
        // 변수명을 주입할 빈 이름과 동일하게 맞추는 방법, @Qualifier 어노테이션으로 구체적으로 주입할 빈의 이름이 뭔지 지정하는 방법 외에 또 다른 방법!
        @Primary
        @Bean
        public ProxyFactoryBean cachingMovieReaderFactory(ApplicationContext applicationContext){
            MovieReader target = applicationContext.getBean(MovieReader.class);
            CacheManager cacheManager = applicationContext.getBean(CacheManager.class);

            // 자바에는 JDK에서 제공하는 동적 프락시 외에도 프락시를 다룰 수 있는 다양한 기술이 존재. (ex. CGLIB, Javassist, Byte Buddy)
            // 스프링의 AOP 모듈은 이런 다양한 프락시 기술들을 일관된 방식으로 사용할 수 있도록 ProxyFactoryBean 이라는 서비스 추상화를 제공.
            // ProxyFactoryBean은 프락시를 생성해서 빈 객체를 등록해 주는 팩토리 빈.
            // JDK 동적 프락시와는 달리, 프락시 팩토리 빈은 순수하게 프락시를 생성하는 작업만 담당하고, 프락시를 통해 제공할 부가 기능은 별도 객체로 생성해 스프링 컨테이너에 빈으로 등록해 줄 수 있다.
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setTarget(target);

            /** cf. ProxyFactoryBean 은 기본적으로 JDK가 제공하는 동적 프락시를 통해 프락시를 만들어 낸다.
                JDK 동적 프락시는 인터페이스를 기반으로 프락시를 생성하는데, 경우에 따라 클래스 기반의 프락시를 생성해야할 때가 있다.(인터페이스가 아닌 특정 클래스 타입으로 의존관계 주입을 받아야하는 경우)
                아래 옵션을 활성화 시켜주면 클래시 프락시를 생성할 수 있는데,
                이 클래스 프락시는 CGLIB 이라고 하는 오픈 소스 바이트 코드 생성 라이브러리를 이용해, 대상 객체 타입을 상속해서 서브 클래스로 만들어 이를 프락시로 사용.
                서브 클래스도 자신이 상속한 대상 객체와 같은 타입이니까 클라이언트에게 의존 관계 주입이 가능하다라는 원리를 이용한 것.
                But, 클래스 프락시의 2가지 제약 : 1. final 클래스와 final 메소드에는 적용이 안 된다. 2. 대상 클래스의 생성자가 두 번 호출된다.
                      final 클래스는 상속을 할 수 없고, final 메소드는 오버라이딩이 불가능하기 때문. 또 같은 대상 클래스 타입의 빈이 두 개가 만들어지기 때문에 생성자가 두 번 호출.
                이 방식은 인터페이스 기반 프락시를 생성하는 것에 비해 굉장히 부자연스러움. 동적으로 클래스를 상속할뿐더러 이 상속받은 객체의 public 메소드를 모두 오버라이드 해서 프락시 기능으로 바꿔치는 방식으로 동작하기 때문.

                스프링은 인터페이스가 없이 개발된 레거시 코드나 외부에서 개발된 인터페이스 없는 라이브러리 같은 것들을 이 프락시 기법을 적용할 수 있도록 지원해 주기 위해 이 클래스 프락시를 지원하는 것.
                결코 인터페이스를 사용하지 않고 클래스만 사용해서 프로그래밍을 하다가 프락시가 필요한 경우에 이를 이용하라는 의미가 아님.
                --> 스프링은 인터페이스를 통한 객체 지향적인 합성 기법과 이를 바탕에 둔 제어의 역전과 의존 관계 주입을 사용하는 것을 권장. */
            // 클래스 프락시 활성화(true)/비활성화(false, 기본값)
            //proxyFactoryBean.setProxyTargetClass(true);
            proxyFactoryBean.addAdvice(new CachingAdvice(cacheManager));

            return proxyFactoryBean;
        }
    }
}
