package moviebuddy.domain;

import moviebuddy.MovieBuddyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


 /**
 * cf. Bean Scope
 * 스프링 컨테이너가 빈을 생성할 때
 * - Singleton Scope 이면, 단 하나의 빈 객체만 생성.
 * - prototype Scope 이면, 빈이 요청될 때마다 새로운 빈 생성.
 * -> 빈을 설정할 때 별도로 설정을 해 주지 않으면 기본적으로 Singleton Scope 가 지정됨.
 *    Singleton Scope는 모든 빈의 기본 Scope로 스프링 컨테이너가 시작될 때 생성되고, 종료될 때 소멸.
 */
public class BeanScopeTests {

    @Test
    void Equals_MovieFinderBean(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MovieBuddyFactory.class);
        MovieFinder movieFinder = applicationContext.getBean(MovieFinder.class);

        Assertions.assertEquals(movieFinder, applicationContext.getBean(MovieFinder.class));
    }
}
