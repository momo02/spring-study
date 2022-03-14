package moviebuddy.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

/**
 * @AspectJ 애노테이션 스타일
 * ✔ @AspectJ의 문법과 애스펙트 정의 방법을 활용해 스프링 AOP를 적용하는 방법.
 * ✔ POJO 클래스에 포인트컷과 다양한 어드바이드 애노테이션을 이용해 애스펙트를 작성.
 * ✔ AspectJ 런타임 라이브러리인 aspectjweaver.jar가 필요.
 */
@Aspect
public class CachingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CacheManager cacheManager;

    public CachingAspect(CacheManager cacheManager){
        this.cacheManager = Objects.requireNonNull(cacheManager);
    }

//    @Pointcut("target(moviebuddy.domain.MovieReader)")
//    private void performance() {}

    // advice 애노테이션. 자바 메소드에 @Around, @Before, @After, @AfterReturning, @AfterThrowing 등 애노테이션을 적용해 어드바이스를 작성.
    // 애노테이션 속성으로 어드바이스에 적용할 포인트컷 이름이나 포인트컷 표현식을 작성
//    @Around("performance()")
    @Around("target(moviebuddy.domain.MovieReader)")
    public Object doCachingReturnValue(ProceedingJoinPoint pjp) throws Throwable {
        // 캐시된 데이터가 존재하면, 즉시 반환 처리

        // invocation.getThis()는 대상 객체를 의미.(여기선 CsvMovieReader 또는 XmlMovieReader)
        // 대상 객체의 클래스 명을 캐시 이름으로 지정하여 캐시를 구성.
        Cache cache = cacheManager.getCache(pjp.getThis().getClass().getName());
        // 호출된 메서드의 이름(여기선 loadMovies)을 key로 캐시에 데이터를 저장하거나 취득.
        Object cachedValue = cache.get(pjp.getSignature().getName(), Object.class);
        if(Objects.nonNull(cachedValue)){
            log.info("returns cached data. [" + pjp + "]");
            return cachedValue;
        }

        // 캐시된 데이터가 없으면, 대상 객체에 명령을 위임하고, 반환된 값을 캐시에 저장 후 반환 처리
        cachedValue = pjp.proceed();
        cache.put(pjp.getSignature().getName(), cachedValue);

        log.info("caching return value. [" + pjp + "]");

        return cachedValue;
    }
}
