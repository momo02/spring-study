package moviebuddy.domain;

import moviebuddy.MovieBuddyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * @author springrunner.kr@gmail.com
 */
public class MovieFinderTest {
//	final MovieBuddyFactory movieBuddyFactory = new MovieBuddyFactory();
//	final MovieFinder movieFinder = movieBuddyFactory.movieFinder();

	// annotation을 이용해 빈 구성정보를 읽어 application context를 구성.
	final ApplicationContext applicationContext =
			new AnnotationConfigApplicationContext(MovieBuddyFactory.class);
	final MovieFinder movieFinder = applicationContext.getBean(MovieFinder.class);

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
