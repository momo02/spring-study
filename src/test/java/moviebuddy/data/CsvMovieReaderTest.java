package moviebuddy.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class CsvMovieReaderTest {

    @Test
    void Valid_Metadata() throws Exception {
        CsvMovieReader movieReader = new CsvMovieReader();
        movieReader.setMetadata("movie_metadata.csv");

        movieReader.afterPropertiesSet();
    }

    @Test
    void Invalid_Metadata() {
        CsvMovieReader movieReader = new CsvMovieReader();
        //예외 검증
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            movieReader.setMetadata("invalid");
            movieReader.afterPropertiesSet();
        });
    }
}
