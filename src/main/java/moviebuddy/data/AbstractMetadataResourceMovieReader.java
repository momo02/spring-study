package moviebuddy.data;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import moviebuddy.ApplicationException;
import moviebuddy.domain.MovieReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

// 스프링 빈 초기화 시점이나 소멸 시점에 사용할 수 있는 콜백 인터페이스 InitializingBean, DisposableBean 처럼
// ResourceLoaderAware 도 스프링 제공 콜백 인터페이스 중 하나.
// 의존 관계 주입을 받을 때 쓸 수 있는 콜백 인터페이스로, ResourceLoader를 외부에서 주입 받을 수 있도록 setter 메소드 하나가 정의되어 있다.
public abstract class AbstractMetadataResourceMovieReader implements MovieReader, ResourceLoaderAware {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private String metadata;

	private ResourceLoader resourceLoader;

	public String getMetadata() {
		return metadata;
	}

    @Value("${movie.metadata}")
	public void setMetadata(String metadata) {
		this.metadata = Objects.requireNonNull(metadata, "metadata is a required value.");
	}

//	public URL getMetadataUrl() {
//		String location = getMetadata();
//		if(location.startsWith("file:")) {
//			// file URL 처리
//		}else if(location.startsWith("http:")) {
//			// http URL 처리
//		}
//		return ClassLoader.getSystemResource(location);
//	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Resource getMetadataResource() {
		return resourceLoader.getResource(getMetadata());
	}

	@PostConstruct
	public void afterPropertiesSet() throws Exception {

		// URL metadataUrl = ClassLoader.getSystemResource(getMetadata());
		// ClassLoader.getSystemResource(getMetadata()) -> 클래스패스 상의 자원만 처리할 수 있다.

		// 만약 애플리케이션 외부 파일이나 네트워크 상의 파일을 읽어야 한다면? (ex. file:~, http:~, ftp:~)
		// 프로토콜에 따라서 URL 객체를 다루기 위한 방식이 변경이 되어야 할 것.
		// 이것도 한계가 존재함. 자바의 URL 클래스로는 자바 웹 애플리케이션 개발 시 사용되는 서블릿 컨텍스트 경로나 클라우드 스토리지 서비스에 있는 자원과 같은 것들은 표현할 수 없기 때문.
		// =>>> 그래서 스프링은 파일이나 원격지에 있는 자원에 일관된 방식으로 접근하고 사용할 수 있는 추상화 인터페이스로 리소스 인터페이스를 제공.

//		URL metadataUrl = getMetadataUrl();
//		if (Objects.isNull(metadataUrl)) {
//			throw new FileNotFoundException(metadata);
//		}
//
//		if (Files.isReadable(Path.of(metadataUrl.toURI())) == false) {
//			throw new ApplicationException(String.format("cannot read to metadata. [%s]", metadata));
//		}

		Resource resource = getMetadataResource();
		if(!resource.exists()) {
			throw new FileNotFoundException(metadata);
		}
		if(!resource.isReadable()){
			throw new ApplicationException(String.format("cannot read to metadata. [%s]", metadata));
		}

		log.info(resource + " is ready.");
	}

	@PreDestroy
	public void destroy() throws Exception {
	    log.info("Destroyed bean");
	}

}