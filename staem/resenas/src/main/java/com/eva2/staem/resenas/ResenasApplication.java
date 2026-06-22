package com.eva2.staem.resenas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.eva2.staem")
@EnableDiscoveryClient
public class ResenasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResenasApplication.class, args);
    }
}


