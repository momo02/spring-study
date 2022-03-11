package moviebuddy.data;

import moviebuddy.domain.Movie;
import moviebuddy.domain.MovieReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.ArrayList;
import java.util.List;

public class CachingMovieReaderTest {

    @Test
    void caching() {
        CacheManager cacheManager = new ConcurrentMapCacheManager();
        MovieReader target = new DummyMovieReader();

        CachingMovieReader movieReader = new CachingMovieReader(cacheManager,target);
        // 아직 캐시에 저장된 값이 없으므로 null
        Assertions.assertNull(movieReader.getCachedData());

        // loadMovies를 호출하면 반환 받을 값을 캐시에 저장.
        List<Movie> movies = movieReader.loadMovies();
        // 캐시는 이제 null 이 아님
        Assertions.assertNotNull(movieReader.getCachedData());
        // 다시 loadMovies를 호출하여 캐시에 저장된 값과 앞서 반환 받은 값이 같은지 비교.
        Assertions.assertSame(movieReader.loadMovies(), movies);
    }

    class DummyMovieReader implements MovieReader {

        @Override
        public List<Movie> loadMovies() {
            return new ArrayList<>();
        }
    }
}
