package moviebuddy;

import moviebuddy.domain.CsvMovieReader;
import moviebuddy.domain.MovieFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Configuration 어노테이션을 붙임으로,
 * 스프링의 빈 구성정보 => Configuration Metadata로 사용이 될 수 있음을 선언.
 */
@Configuration
public class MovieBuddyFactory {

    @Bean
    public MovieFinder movieFinder(){
        return new MovieFinder(new CsvMovieReader());
    }
}
