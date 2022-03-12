package moviebuddy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * java 기본 플랫폼에 내장된 reflection API는 동적으로 프락시를 생성할 수 있는 기능이 제공됨.
 * reflection은 자바의 코드 자체를 추상화해서 접근할 수 있도록 만들어진 API로, 객체의 메서드나 필드 등의 정보를 추출하거나 이용할 수 있다.
 *
 * [java reflection 학습 테스트]
 * -> 리플랙션은 매우 강력한 동시에 다루기 까다로운 어려운 도구이기 때문에
 *    가능한 한 직접 리플렉션 API를 사용해서 코드를 작성하는 것보다, 검증된 프레임워크나 라이브러리를 통해 간접적으로 사용하는 것이 좋다.
 */
public class ReflectionTests {

    @Test
    void objectCreatedAndMethodCall() throws Exception {
        // Without reflection
        Duck duck = new Duck();
        duck.quack();

        // With reflection
        Class<?> duckClass = Class.forName("moviebuddy.ReflectionTests$Duck");
        Object duckObject = duckClass.getDeclaredConstructor().newInstance();
        Method quackMethod = duckObject.getClass().getDeclaredMethod("quack", new Class<?>[0]);
        quackMethod.invoke(duckObject);
    }

    static class Duck {
        void quack() {
            System.out.println("꽥꽥");
        }
    }
}