package com.eva2.staem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StaemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaemApplication.class, args);
	}

}
