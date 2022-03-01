package moviebuddy;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Configuration 어노테이션을 붙임으로,
 * 스프링의 빈 구성정보 => Configuration Metadata로 사용이 될 수 있음을 선언.
 */
@Configuration
// 스프링은 자동 클래스 탐지 기법으로 스프링 컨테이너에 빈을 자동으로 등록해 주는 기능을 제공.
// @ComponentScan 을 통해 활성화. -> 지정된 패키지 경로에서 @Component 와 같이 스테레오 타입으로 선언된 클래스를 찾아 빈으로 동록하고 관리.
// 패키지를 지정하지 않으면 @ComponentScan 이 선언된 클래스의 패키지 경로를 기준으로 탐색.
@ComponentScan(basePackages = { "moviebuddy" })
@Import({ MovieBuddyFactory.DomainModuleConfig.class, MovieBuddyFactory.DataSourceModuleConfig.class })
public class MovieBuddyFactory {

    @Configuration
    static class DomainModuleConfig {

    }

    @Configuration
    static class DataSourceModuleConfig {

    }
}
