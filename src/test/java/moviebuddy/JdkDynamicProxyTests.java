package moviebuddy;

import moviebuddy.data.CsvMovieReader;
import moviebuddy.domain.MovieReader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * CsvMovieReader 에서 프락시(CachingMovieReader) 를 이용해 부가 기능(캐싱)을 깔끔하게 분리했다.
 * 프락시는 기존 코드에 영향을 주지 않으면서 대상 객체의 기능을 확장하거나 접근 방법을 제어할 수 있는 유용한 방법이지만 문제점이 있다.
 * 만약 캐시 부가 기능을 MovieReader가 아닌 다른 객체(ex.MovieFinder)에 적용하려면..
 * 프락시 구조를 만들기 위해 필요한 인터페이스를 비롯해, 부가 기능을 구현한 클래스의 작성, 프락시 생성을 위한 코드 작성 등의 과정을 또다시 반복해야한다.
 * => 즉 프락시를 만드는 과정이 상당히 번거롭다.
 * 또한 프락시를 통해 제공하는 부가 기능 또한 중복될 가능성이 매우 높다.
 * 캐싱, 로깅, 보안, 트랜잭션과 같은 횡단 관심사를 해결하는 부가 기능들은 애플리케이션 전반에 걸쳐 다양한 곳에서 사용될 것인데
 * 필요한 객체 마다 프락시 클래스를 만든다면 결국 유사 코드가 여러 클래스에 중복되서 나타날 것.
 *
 * ===> 위 문제를 극복하기 위해 자바에는 동적으로 프락시를 생성할 수 있는 기술이 있다.
 * [동적 프락시 기술]을 사용하면 프락시를 위한 별도 클래스를 작성하지 않아도 되고, 부가 기능 또한 대상 객체와 무관하게 재사용할 수 있다.
 * -> 덕분에 프락시 기법의 번잡함이나 부가 기능 코드 중복 등의 문제를 해결.
 * 다만 한게는 여전히 있다.
 * - 대상 객체에 부가 기능을 부여하고 프락시를 생성하는 코드는 꽤나 복잡하고 다루기가 어렵다.
 * - 대상 객체에 부가 기능을 부여하기 위해서는 매번 부가 기능 객체를 만들고 (Proxy.newProxyInstance) 대상 객체에 의존 관계 주입을 해줘야 한다.
 * - 또한 자바에는 JDK에서 제공하는 동적 프락시 외에도 프락시를 다룰 수 있도록 지원해주는 다양한 기술들이 존재하는데,
 *   이렇게 특정 기술을 이용해 동적 프락시를 생성하면 추후 기술의 한계나 다른 필요에 의해 프락시 기술에 대한 변경이 어려워진다.
 *
 * ===> 스프링이 제공하는 AOP 모듈을 이용해 더 쉽고 깔끔한 방식으로 프락시를 다룰 수 있다.
 */
public class JdkDynamicProxyTests {

    @Test
    void useDynamicProxy() throws Exception {
        CsvMovieReader movieReader = new CsvMovieReader();
        movieReader.setResourceLoader(new DefaultResourceLoader());
        movieReader.setMetadata("movie_metadata.csv");
        movieReader.afterPropertiesSet(); // 설정한 메타데이터가 올바른 데이터인지 검증해주는 코드 실행.

        ClassLoader classLoader = JdkDynamicProxyTests.class.getClassLoader();
        // 대상이 되는 인터페이스 타입 (프락시로 구성해야하는 인터페이스)
        Class<?>[] interfaces = new Class[] { MovieReader.class };
        InvocationHandler handler = new PerformanceInvocationHandler(movieReader);

        // JDK 동적 프락시를 이용해 호출 메서드의 수행시간을 측정하는 프락시 객체를 생성.
        MovieReader proxy = (MovieReader) Proxy.newProxyInstance(classLoader, interfaces, handler);
        proxy.loadMovies();
        proxy.loadMovies();
    }

    // 프락시 객체에 loadMovies 메소드가 호출되면 이 호출은 PerformanceInvocationHandler 로 연결된다.
    static class PerformanceInvocationHandler implements InvocationHandler {
        final Logger log = LoggerFactory.getLogger(getClass());
        final Object target;

        PerformanceInvocationHandler(Object target){
            this.target = Objects.requireNonNull(target);
        }

        // 실행된 프락시 객체, 실행된 메서드, 메서드를 실행할 때 넘겨받은 인자들을 받는다.
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long start = System.currentTimeMillis();
            Object result = method.invoke(target, args); //실제 대상 객체의 메서드를 호출
            long elapsed = System.currentTimeMillis() - start;

            log.info("Execution {} method finished in {} ms", method.getName(), elapsed);

            return result;
        }
    }
}
