package moviebuddy.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

/**
 * CachingAdvice는 순수하게 부가 기능에 관련된 로직(데이터 캐싱 기능)만 담겨 있어서 재사용하기에 용이.
 */
public class CachingAdvice implements MethodInterceptor {

    private final CacheManager cacheManager;

    public CachingAdvice(CacheManager cacheManager){
        this.cacheManager = Objects.requireNonNull(cacheManager);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 캐시된 데이터가 존재하면, 즉시 반환 처리

        // invocation.getThis()는 대상 객체를 의미.(여기선 CsvMovieReader 또는 XmlMovieReader)
        // 대상 객체의 클래스 명을 캐시 이름으로 지정하여 캐시를 구성.
        Cache cache = cacheManager.getCache(invocation.getThis().getClass().getName());
        // 호출된 메서드의 이름(여기선 loadMovies)을 key로 캐시에 데이터를 저장하거나 취득.
        Object cachedValue = cache.get(invocation.getMethod().getName(), Object.class);
        if(Objects.nonNull(cachedValue)){
            return cachedValue;
        }

        // 캐시된 데이터가 없으면, 대상 객체에 명령을 위임하고, 반환된 값을 캐시에 저장 후 반환 처리
        cachedValue = invocation.proceed();
        cache.put(invocation.getMethod().getName(), cachedValue);
        return cachedValue;
    }
}
