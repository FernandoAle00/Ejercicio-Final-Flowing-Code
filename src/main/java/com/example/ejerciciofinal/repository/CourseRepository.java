package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Professor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByProfessor(Professor professor);
    
    List<Course> findByProfessorId(Long professorId);
    
    /**
     * Obtiene todos los cursos paginados con Professor y Seats cargados (EAGER)
     * Evita LazyInitializationException al acceder a professor.getName() y seats.size()
     */
    @Override
    @EntityGraph(attributePaths = {"professor", "seats"})
    Page<Course> findAll(Pageable pageable);
    
    /**
     * Obtiene todos los cursos (sin paginación) con Professor y Seats cargados (EAGER)
     * Usado para ComboBox y selects donde se necesita la lista completa
     * Evita LazyInitializationException al acceder a professor.getName() y seats
     */
    @Override
    @EntityGraph(attributePaths = {"professor", "seats"})
    List<Course> findAll();
    
}
