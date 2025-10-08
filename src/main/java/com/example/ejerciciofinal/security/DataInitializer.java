package com.example.ejerciciofinal.security;

import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.model.User;
import com.example.ejerciciofinal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializa datos por defecto en la aplicación
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Crear usuario ADMIN por defecto si no existe
        if (!userRepository.existsByUserName("admin")) {
            User admin = new User("admin", "admin123", Role.ADMIN, null);
            userRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: username=admin, password=admin123");
        } else {
            System.out.println("ℹ️ Usuario ADMIN ya existe");
        }
    }
}
