package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.model.Seat;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findByStudent(Student student);
    
    List<Seat> findByCourse(Course course);
    
    List<Seat> findByStudentId(Long studentId);
    
    List<Seat> findByCourseId(Long courseId);
    
    Optional<Seat> findByStudentIdAndCourseIdAndYear(Long studentId, Long courseId, LocalDate year);
    
}
