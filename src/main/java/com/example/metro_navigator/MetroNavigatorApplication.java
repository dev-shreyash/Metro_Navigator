package com.example.metro_navigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MetroNavigatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetroNavigatorApplication.class, args);
	}

}
