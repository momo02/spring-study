package moviebuddy.domain;

import javax.cache.annotation.CacheResult;
import java.util.List;

/**
 * MovieReader 인터페이스를 domain 패키지에 포함하여, domain 패키지를 완벽하게 독립시킬 수 있게 됨.
 * -> 이렇게 추상화를 별도의 독립적인 패키지가 아니라 클라이언트에 속한 패키지에 포함하는 구조를 분리된 인터페이스 패턴(Seperated Interface Pattern)이라 부른다.
 */
public interface MovieReader {

    @CacheResult(cacheName = "movies")
    List<Movie> loadMovies();
}
