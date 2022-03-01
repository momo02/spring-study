package moviebuddy.domain;

import moviebuddy.MovieBuddyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

/**
 * @author springrunner.kr@gmail.com
 */

@SpringJUnitConfig(MovieBuddyFactory.class) //아래 주석 처리한 어노테이션들을 포함하고 있음.
///**
// * - @ExtendWith : JUnit이 테스트 실행 전략을 확장할 때 사용하는 어노테이션.
// * - @ExtendWith 에 제공한 SpringExtension 클래스는 스프링의 테스트 컨텍스트 프레임워크에서 제공하는 JUnit 지원 클래스로,
// * 	 JUnit이 테스트를 실행하는 과정에서 테스트가 필요로 하는 스프링 컨테이너를 구성하고 관리해 줌.
// * - 스프링 컨테이너를 구성할 때, 이 @ContextConfiguration 어노테이션이 지정되어 있다면
// * 	 해당 어노테이션이 지정한 빈 구성 정보를 바탕으로 스프링 컨테이너를 만든다.
// */
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = MovieBuddyFactory.class)
public class MovieFinderTest {

// 스프링의 테스트 컨텍스트 프레임워크에 의해 애플리케이션 컨텍스트, 즉 스프링 컨테이너가 구성될 거기 때문에
// 이제 직접 스프링의 애플리케이션 컨텍스트를 생성하는 코드는 제거한다.
//	final ApplicationContext applicationContext =
//			new AnnotationConfigApplicationContext(MovieBuddyFactory.class);
//	final MovieFinder movieFinder = applicationContext.getBean(MovieFinder.class);

	// 이제는 스프링이 제공하는 의존 관계 주입 기능을 활용.
	// 1. 생성자를 통해 주입.
//	final MovieFinder movieFinder;
//
//	@Autowired
//	MovieFinderTest(MovieFinder movieFinder){
//		this.movieFinder = movieFinder;
//	}

	// 2. setter 메서드를 이용해 주입.
//	MovieFinder movieFinder;
//
//	@Autowired
//	void setMovieFinder(MovieFinder movieFinder) {
//		this.movieFinder = movieFinder;
//	}

	// 3. 필드 레벨에 @Autowired 어노테이션을 붙여 주입.
	// cf. 실제 운영소스에서는 이 방법을 사용하지 않는 것을 권장. 이렇게 필드레벨에서 의존 관계 주입을 받도록 코드를 작성하면 테스트를 하는것이 힘들어진다.
	@Autowired MovieFinder movieFinder;


	@Test
	void NotEmpty_DirectedBy() {
		List<Movie> movies = movieFinder.directedBy("Michael Bay");
		Assertions.assertEquals(3, movies.size());
	}
	
	@Test
	void NotEmpty_ReleasedYearBy(){
		List<Movie> movies = movieFinder.releasedYearBy(2015);
		Assertions.assertEquals(225, movies.size());
	}
}
