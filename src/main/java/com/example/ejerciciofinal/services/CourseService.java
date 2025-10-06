package com.example.ejerciciofinal.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejerciciofinal.dtos.CreateCourseDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.dtos.ResponseCourseDTO;
import com.example.ejerciciofinal.dtos.ResponseUserDTO;
import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Seat;
import com.example.ejerciciofinal.model.User;
import com.example.ejerciciofinal.repository.CourseRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public ResponseCourseDTO createCourse(CreateCourseDTO courseDTO) {

        validateCourseDTO(courseDTO);


        // Crear User
        Set<Seat> seats = courseDTO.getSeats().isEmpty() ? Set.of() : courseDTO.getSeats();
        
        Professor professor = (Professor) userService.getPersonById(courseDTO.getProfessor().getId());
        
        Course course;
        course = new Course(
                courseDTO.getName(),
                professor,
                seats
        );

        Course savedCourse = courseRepository.save(course);

        
        return new ResponseCourseDTO(
                savedCourse.getName(),
                courseDTO.getProfessor(),
                courseDTO.getSeats()
        );
    }

    private void validateCourseDTO(CreateCourseDTO courseDTO) {
        if (courseDTO == null) {
            throw new IllegalArgumentException("El objeto courseDTO no puede ser nulo");
        }
        if (courseDTO.getName() == null || courseDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio");
        }
        if (courseDTO.getProfessor() == null) {
            throw new IllegalArgumentException("El profesor es obligatorio");
        }
    }

}
