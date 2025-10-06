package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByProfessor(Professor professor);
    
    List<Course> findByProfessorId(Long professorId);
    
}
