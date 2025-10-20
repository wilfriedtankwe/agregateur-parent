package com.agregateur.dimsoft.agregateur_production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgregateurProductionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgregateurProductionApplication.class, args);
	}

}


