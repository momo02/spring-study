package moviebuddy;

import moviebuddy.domain.CsvMovieReader;
import moviebuddy.domain.MovieFinder;
import moviebuddy.domain.MovieReader;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Configuration 어노테이션을 붙임으로,
 * 스프링의 빈 구성정보 => Configuration Metadata로 사용이 될 수 있음을 선언.
 */
@Configuration
public class MovieBuddyFactory {

    @Bean
    public MovieReader movieReader() {
        return new CsvMovieReader();
    }

    // 의존관계 주입 - 1. 메소드 콜 방식
//    @Bean
//    public MovieFinder movieFinder(){
//        return new MovieFinder(movieReader());
//    }

    // 의존관계 주입 - 2. 메소드 파라미터 방식 (스프링 컨테이너가 파라미터인 MovieReader 빈이 등록되어있는지 확인하고 생성)
    @Bean
    //@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) //prototype scope로 변경.
    public MovieFinder movieFinder(MovieReader movieReader){
        return new MovieFinder(movieReader);
    }
}
