package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.model.Professor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    Optional<Professor> findByEmail(String email);
    
    /*
     * Buscar un profesor por ID con carga eager de courses, courses.seats y address
     * evita lazyinitializationexception al acceder a las colecciones fuera de la transacción
     * courses.seats es necesario para calcular cupos ocupados/disponibles
     */
    @Override
    @EntityGraph(attributePaths = {"courses", "courses.seats", "address"})
    Optional<Professor> findById(Long id);
    
}
