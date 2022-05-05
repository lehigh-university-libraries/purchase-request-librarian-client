package edu.lehigh.libraries.purchase_request.librarian_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConditionalOnWebApplication
@Slf4j
public class LibrarianApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LibrarianApplication.class);
	}

	public static void main(String[] args) throws Exception {
		log.info("Starting the Librarian Application");
		SpringApplication.run(LibrarianApplication.class, args);
	}

}
