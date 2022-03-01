package moviebuddy;

import moviebuddy.domain.CsvMovieReader;
import moviebuddy.domain.MovieFinder;
import moviebuddy.domain.MovieReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Configuration 어노테이션을 붙임으로,
 * 스프링의 빈 구성정보 => Configuration Metadata로 사용이 될 수 있음을 선언.
 */
@Configuration
//Import 어노테이션을 통해 MovieBuddyFactory가 불러와야할 빈 구성 정보를 명시해준다.
// - Import 어노테이션은 다른 클래스에서 빈 구성 정보를 불러오기 위해서 사용.
// - 만약 XML 형식으로 작성된 빈 구성 정보가 있다면 @ImportResource("xml파일위치") 어노테이션을 통해 불러옴.
// 지금 이 코드는 이해를 돕기 위해 하나의 클래스에 여러 개의 빈 구성 정보를 작성하고 조합하는 방법을 보여준 것.
// => 용도에 따라서 여러 개의 빈 구성 정보를 작성하고 Import 어노테이션을 이용해 한 번에 불러다 쓸 수 있다.
@Import({ MovieBuddyFactory.DomainModuleConfig.class, MovieBuddyFactory.DataSourceModuleConfig.class })
public class MovieBuddyFactory {

    @Configuration
    static class DomainModuleConfig {
        @Bean
        public MovieFinder movieFinder(MovieReader movieReader){
            return new MovieFinder(movieReader);
        }
    }

    @Configuration
    static class DataSourceModuleConfig {
        @Bean
        public MovieReader movieReader() {
            return new CsvMovieReader();
        }
    }
}
