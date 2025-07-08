package com.taller.publicaciones.config;

import com.taller.publicaciones.model.Estado;
import com.taller.publicaciones.repository.EstadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatosIniciales(EstadoRepository estadoRepo) {
        return args -> {
            if (estadoRepo.count() == 0) {
                estadoRepo.save(new Estado(1, "Publicado"));
                estadoRepo.save(new Estado(2, "Borrador"));
                estadoRepo.save(new Estado(3, "Rechazado"));
            }
        };
    }
} 