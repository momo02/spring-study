package moviebuddy;

import moviebuddy.domain.CsvMovieReader;
import moviebuddy.domain.MovieFinder;

/**
 * cf.
 * 객체의 생성방법을 결정하고 생성한 객체를 반환하는 역할을 수행하는 객체를 보편적으로 Factory라 부른다.
 * Factory는 주로 객체를 생성하는 쪽과 생성된 객체를 사용하는 쪽의 역할과 책임을 분리하려는 목적으로 사용.
 *
 * # 팩토리 도입 이후 뒤바뀐 제어 흐름.
 * Factory를 도입한 이후, MovieBuddyApplication은 Factory로 부터 MovieFinder객체를 취득해서 사용.
 * MovieFinder는 Factory에 의해 만들어지고 Factory가 공급하는 MovieReader객체를 사용해 동작.
 * => 제어의 역전 원리가 적용된 코드는 객체가 자신이 사용할 객체를 스스로 결정하지 않고 생성도 하지 않음.
 *    또한 스스로도 언제 어떻게 생성되고 사용되는지를 알 수 없음.
 *    제어 권한을 위임 받은 객체(Factory)에 의해 결정되고 만들어진다.
 */
public class MovieBuddyFactory {

    public MovieFinder movieFinder(){
        return new MovieFinder(new CsvMovieReader());
    }
}
