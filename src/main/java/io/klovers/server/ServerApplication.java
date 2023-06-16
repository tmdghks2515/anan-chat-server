package io.klovers.server;

import io.klovers.server.common.resolvers.UserArgumentResolver;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public UserArgumentResolver userDataArgumentResolver() {
		return new UserArgumentResolver();
	}

	@PostConstruct
	public void initialize() {
		// timezone 셋팅
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
