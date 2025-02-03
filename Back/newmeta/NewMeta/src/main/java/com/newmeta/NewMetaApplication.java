package com.newmeta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewMetaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewMetaApplication.class, args);
	}

}
