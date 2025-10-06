package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByStudentNumber(UUID studentNumber);
    
    Optional<Student> findByEmail(String email);
    
}
