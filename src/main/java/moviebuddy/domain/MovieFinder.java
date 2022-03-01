package moviebuddy.domain;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MovieFinder {
    private final MovieReader movieReader;

    // @Autowired 어노테이션을 이용해 의존 관계 주입을 자동으로 받을 수 있도록 설정.
    // @Autowired  // 생성자가 하나뿐이라면 생략 가능.
    public MovieFinder(@Qualifier("csvMovieReader") MovieReader movieReader){
        this.movieReader = Objects.requireNonNull(movieReader);
    }
    // 기본적으론 선언되어 있는 타입(MovieReader)을 기반으로 의존 관계 주입 대상을 찾는다.
    // 만약 MovieReader 타입의 빈이 2개 이상 존재할 경우엔, 지정한 변수명(movieReader)과 동일한 빈 이름으로 찾는다.

    /**
     * 저장된 영화 목록에서 감독으로 영화를 검색한다.
     *
     * @param directedBy 감독
     * @return 검색된 영화 목록
     */
    public List<Movie> directedBy(String directedBy) {
        return movieReader.loadMovies().stream()
                .filter(it -> it.getDirector().toLowerCase().contains(directedBy.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 저장된 영화 목록에서 개봉년도로 영화를 검색한다.
     *
     * @param releasedYearBy
     * @return 검색된 영화 목록
     */
    public List<Movie> releasedYearBy(int releasedYearBy) {
        return movieReader.loadMovies().stream()
                .filter(it -> Objects.equals(it.getReleaseYear(), releasedYearBy))
                .collect(Collectors.toList());
    }


}
