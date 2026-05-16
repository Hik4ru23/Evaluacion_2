package com.eva2.staem.amigos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.eva2.staem")
public class AmigosApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmigosApplication.class, args);
    }
}

