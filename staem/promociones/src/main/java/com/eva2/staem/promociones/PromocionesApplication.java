package com.eva2.staem.promociones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication(scanBasePackages = "com.eva2.staem")
@EnableDiscoveryClient
public class PromocionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromocionesApplication.class, args);
    }
}



