package moviebuddy.data;

import moviebuddy.MovieBuddyFactory;
import moviebuddy.MovieBuddyProfile;
import moviebuddy.domain.Movie;
import moviebuddy.domain.MovieReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.AopTestUtils;

import java.util.List;

@ActiveProfiles(MovieBuddyProfile.XML_MODE)
@SpringJUnitConfig(MovieBuddyFactory.class)
// 테스트를 위한 프로퍼티 소스를 제공. (파일 생성 없이, 아래와 같이 간단하게 설정 가능)
@TestPropertySource(properties = "movie.metadata=movie_metadata.xml")
public class XmlMovieReaderTest {

    // ProxyFactoryBean 은 기본적으로 JDK가 제공하는 동적 프락시를 통해 프락시를 만들어 냄 -> JDK 동적 프락시는 '인터페이스'를 기반으로 프락시를 생성.
    // 즉 현재 MovieReader 인터페이스를 기반으로 프락시가 생성되었는데, XmlMovieReader 타입의 빈을 의존관계 주입을 받으려하니, 타입 불일치로 의존관계 주입이 안되어 테스트 실패.

    // 이를 해결하기 위한 방법 2가지
    // 1. 프락시 생성을 JDK 동적 프락시가 아니라 CGLIB을 이용해서 클래스 프락시로 만드는 것.
    // 2. 의존관계 주입할 빈의 타입을 인터페이스 타입인 MovieReader 로 바꿔주는 것. (=> 스프링이 권장하는 방식인 인터페이스를 통한 의존 관계 주입으로 변경해주는 것이 더 좋다.)
    @Autowired
    MovieReader movieReader;

    @Test
    void NotEmpty_LoadedMovies() {
        List<Movie> movies = movieReader.loadMovies();

        Assertions.assertEquals(1375,movies.size());
    }

    @Test
    void Check_MovieReaderType() {
        // 현재 주입된 MovieReader 객체가 프락시 객체인지 검증.
        Assertions.assertTrue(AopUtils.isAopProxy(movieReader));

        // 프락시 객체가 맞다면, 그 프락시 객체 내부에 들어있는 대상 객체를 꺼내
        // 대상 객체의 클래스가 XmlMovieReader 타입인지 검증.
        MovieReader target = AopTestUtils.getTargetObject(movieReader);
        Assertions.assertTrue(XmlMovieReader.class.isAssignableFrom(target.getClass()));
    }
}
