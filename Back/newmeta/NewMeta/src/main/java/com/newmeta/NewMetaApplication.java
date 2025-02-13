package com.newmeta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewMeta2Application {

	public static void main(String[] args) {
		SpringApplication.run(NewMeta2Application.class, args);
	}

}
