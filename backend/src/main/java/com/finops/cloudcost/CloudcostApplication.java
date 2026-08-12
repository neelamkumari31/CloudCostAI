package com.finops.cloudcost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudcostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudcostApplication.class, args);
	}

}
