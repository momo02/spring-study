package moviebuddy.data;

import moviebuddy.MovieBuddyFactory;
import moviebuddy.MovieBuddyProfile;
import moviebuddy.domain.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

@ActiveProfiles(MovieBuddyProfile.XML_MODE)
@SpringJUnitConfig(MovieBuddyFactory.class)
// 테스트를 위한 프로퍼티 소스를 제공. (파일 생성 없이, 아래와 같이 간단하게 설정 가능)
@TestPropertySource(properties = "movie.metadata=movie_metadata.xml")
public class XmlMovieReaderTest {

    @Autowired
    XmlMovieReader movieReader;

    @Test
    void NotEmpty_LoadedMovies() {
        List<Movie> movies = movieReader.loadMovies();

        Assertions.assertEquals(1375,movies.size());
    }
}
