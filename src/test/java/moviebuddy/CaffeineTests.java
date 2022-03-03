package moviebuddy;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class CaffeineTests {

    //cf. 캐시와 관련해 반드시 사전에 고려해야할 제한 사항이 있다.
    // 바로 캐시로 사용할 저장소의 용량이다. 현재 메모리 기반의 캐시를 사용하고 있는데, 메모리의 크기가 무한하지 않다는 점을 고려해야 한다.
    // 메모리의 크기는 제한되어 있고, 따라서 모든 객체를 메모리에 저장할 수 없다.
    // 또한 캐시로 사용할 메모리의 크기는 애플리케이션이 동작하는데 영향을 주지않을 정도로 적절한 크기여야 한다.
    // 이 제한 사항을 바탕으로 캐시를 구성할 때는 최대 개수, 만료시간 과 같은 것들을 이용해서 효율적으로 캐시를 사용하도록 하는게 필요한데, 이를 캐시 관리라 부른다.
    // 카페인 라이브러리에도 캐시를 관리할 수 있는 기능을 제공한다.
    @Test
    void useCache() throws InterruptedException {
        Cache<String, Object> cache = Caffeine.newBuilder()
                // 캐시에 작성된 후, 일정 시간 이후에 만료 시키는 옵션. (200 milliseconds가 지나면 만료시켜버림)
                .expireAfterWrite(200, TimeUnit.MILLISECONDS)
                // 최대 백 개까지의 캐시 객체를 캐싱할 수 있다.
                .maximumSize(100)
                .build();

        String key = "springrunner";
        Object value = new Object();

        Assertions.assertNull(cache.getIfPresent(key));

        cache.put(key, value);
        Assertions.assertEquals(value, cache.getIfPresent(key));

        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertEquals(value, cache.getIfPresent(key));

        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertNull(cache.getIfPresent(key)); // 캐시가 만료되어 null 이 되는 지 확인.
    }
}
