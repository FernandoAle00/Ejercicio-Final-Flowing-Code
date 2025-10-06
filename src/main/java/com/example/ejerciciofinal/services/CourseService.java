package com.example.ejerciciofinal.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejerciciofinal.dtos.AddressDTO;
import com.example.ejerciciofinal.dtos.CreateCourseDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.ResponseCourseDTO;
import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Seat;
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

    public ResponseCourseDTO assignStudentsToCourse(Set<Seat> seats, Long courseId) {

        validateSeatsAndCourse(seats, courseId);

        Course courseWithStudents = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el curso con ID: " + courseId));
        courseWithStudents.getSeats().addAll(seats);
        courseWithStudents = courseRepository.save(courseWithStudents);

        CreateUserDTO.ProfessorDTO professorDTO = new CreateUserDTO().new ProfessorDTO(
                courseWithStudents.getProfessor().getName(),
                courseWithStudents.getProfessor().getPhone(),
                courseWithStudents.getProfessor().getEmail(),
                new AddressDTO(
                        courseWithStudents.getProfessor().getAddress().getId(),
                        courseWithStudents.getProfessor().getAddress().getStreet(),
                        courseWithStudents.getProfessor().getAddress().getCity(),
                        courseWithStudents.getProfessor().getAddress().getState(),
                        courseWithStudents.getProfessor().getAddress().getCountry()
                ),
                courseWithStudents.getProfessor().getSalary()
        );

        return new ResponseCourseDTO(
                courseWithStudents.getName(),
                professorDTO,
                courseWithStudents.getSeats()
        );
    }

    private void validateSeatsAndCourse(Set<Seat> seats, Long expectedCourseId) {
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de asientos no puede ser nulo o vacío");
        }
        for (Seat seat : seats) {
            if (seat.getStudent() == null) {
                throw new IllegalArgumentException("Cada asiento debe tener un estudiante asignado");
            }
        }

        List<Seat> invalidSeats = seats.stream()
                .filter(seat -> seat.getCourse() == null || !seat.getCourse().getId().equals(expectedCourseId))
                .toList();

        if (!invalidSeats.isEmpty()) {
            throw new IllegalArgumentException("Todos los asientos deben pertenecer al curso con ID: " + expectedCourseId);
        }

        if (courseRepository.findById(expectedCourseId).isEmpty()) {
            throw new IllegalArgumentException("No se encontró el curso con ID: " + expectedCourseId);
        }
    }
}
