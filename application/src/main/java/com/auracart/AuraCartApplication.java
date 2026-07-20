package com.auracart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AuraCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuraCartApplication.class, args);
	}

}

