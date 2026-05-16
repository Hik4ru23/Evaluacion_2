package com.eva2.staem.pagos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.eva2.staem")
@EnableFeignClients
public class PagosApplication {
    public static void main(String[] args) {
        SpringApplication.run(PagosApplication.class, args);
    }
}

