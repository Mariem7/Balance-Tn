package com.backendspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

import static com.backendspringboot.constant.FileConstant.EMPLOYEE_FOLDER;

@SpringBootApplication
public class BackendSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendSpringBootApplication.class, args);
		new File(EMPLOYEE_FOLDER).mkdirs();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
