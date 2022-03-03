package moviebuddy.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.FileNotFoundException;

public class CsvMovieReaderTest {

    @Test
    void Valid_Metadata() throws Exception {
        //이 테스트 시점에는 정확하게 캐시의 기능을 검증할 필요가 없어 아무런 기능을 수행하지 않는 캐시 매니저를 넣어준다.
        CsvMovieReader movieReader = new CsvMovieReader(new NoOpCacheManager());
        movieReader.setMetadata("movie_metadata.csv");
        movieReader.setResourceLoader(new DefaultResourceLoader());

        movieReader.afterPropertiesSet();
    }

    @Test
    void Invalid_Metadata() {
        CsvMovieReader movieReader = new CsvMovieReader(new NoOpCacheManager());
        movieReader.setResourceLoader(new DefaultResourceLoader());

        //예외 검증
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            movieReader.setMetadata("invalid");
            movieReader.afterPropertiesSet();
        });
    }
}
