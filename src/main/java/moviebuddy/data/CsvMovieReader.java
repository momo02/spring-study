package moviebuddy.data;

import com.github.benmanes.caffeine.cache.Cache;
import moviebuddy.ApplicationException;
import moviebuddy.MovieBuddyProfile;
import moviebuddy.domain.Movie;
import moviebuddy.domain.MovieReader;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

// @Profile 어노테이션을 통해 어떤 프로파일일 때 활성화될 건지를 선언.
// cf. JVM_option_설정 -Dspring.profiles.active=csv_mode
@Profile(MovieBuddyProfile.CSV_MODE)
@Repository
public class CsvMovieReader extends AbstractMetadataResourceMovieReader implements MovieReader, InitializingBean, DisposableBean {

    private final Cache<String, List<Movie>> cache;

    public CsvMovieReader(Cache<String, List<Movie>> cache) {
        this.cache = Objects.requireNonNull(cache);
    }

    /**
     * 영화 메타데이터를 읽어 저장된 영화 목록을 불러온다.
     *
     * @return 불러온 영화 목록
     */
    public List<Movie> loadMovies() {
        // 캐시에 저장된 데이커가 있다면, 즉시 반환한다.
        List<Movie> movies = cache.getIfPresent("csv.movies");
        if(Objects.nonNull(movies) && movies.size() > 0){
            return movies;
        }

        try {
            // InputStream을 취득하기 위해 MetadataResource 는 현재 URL 방식으로 http 리소스(원격지에 있는 리소스)를 가리키고 있음.
            // getInputStream() 메소드가 호출되는 순간 그 http로 지정되어 있는 원격지 리소스를 다운로드해서 그 다운로드한 파일로부터 InputStream 객체를 구성.
            // -> 즉, 취득한 Resource 객체를 '매번 내려받아서' 동작을 하고 있음.

            // 원격지에 있는 리소스를 바라보게 함으로써 메타데이터를 읽는 속도가 굉장히 느려진 상태에서 어떻게 하면 이것을 좀 더 빠르게 만들 수 있을까?
            // -> 캐시(Cache)를 활용한다!
            // cf. 객체 캐싱(object caching) : 생성하는 데 많은 시간이 소비되는 객체를 사용하고 난 뒤에 메모리에서 삭제하는 게 아니라 메모리에 저장을 해두었다가
            //    다시 그 객체가 필요할 때 메모리에서 바로 읽어 옴으로써 객체 생성을 소비하는데 시간을 줄임.
            final InputStream content = getMetadataResource().getInputStream();
            final Function<String, Movie> mapCsv = csv -> {
                try {
                    // split with comma
                    String[] values = csv.split(",");

                    String title = values[0];
                    List<String> genres = Arrays.asList(values[1].split("\\|"));
                    String language = values[2].trim();
                    String country = values[3].trim();
                    int releaseYear = Integer.valueOf(values[4].trim());
                    String director = values[5].trim();
                    List<String> actors = Arrays.asList(values[6].split("\\|"));
                    URL imdbLink = new URL(values[7].trim());
                    String watchedDate = values[8];

                    return Movie.of(title, genres, language, country, releaseYear, director, actors, imdbLink, watchedDate);
                } catch (IOException error) {
                    throw new ApplicationException("mapping csv to object failed.", error);
                }
            };

            movies = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))
                    .lines()
                    .skip(1)
                    .map(mapCsv)
                    .collect(Collectors.toList());
        } catch (IOException error) {
            throw new ApplicationException("failed to load movies data.", error);
        }

        // (캐시에 저장된 데이터가 없으면) 획득한 데이터를 캐시에 저장하고, 반환한다.
        cache.put("csv.movies", movies);
        return movies;
    }
}
