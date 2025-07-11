package com.taller.publicaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PublicacionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublicacionesApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Backend de Publicaciones funcionando correctamente en puerto 8442";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
} 