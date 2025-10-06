package com.example.ejerciciofinal.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejerciciofinal.dtos.CreateCourseDTO;
import com.example.ejerciciofinal.dtos.ResponseCourseDTO;
import com.example.ejerciciofinal.dtos.ResponseSeatDTO;
import com.example.ejerciciofinal.mappers.DTOMapper;
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

        Set<Seat> seats = courseDTO.getSeats().isEmpty() ? Set.of() : courseDTO.getSeats();
        Professor professor = (Professor) userService.getPersonById(courseDTO.getProfessor().getId());

        Course course = new Course(
                courseDTO.getName(),
                professor,
                seats
        );

        Course savedCourse = courseRepository.save(course);

        return new ResponseCourseDTO(
                savedCourse.getName(),
                DTOMapper.toProfessorDTO(savedCourse.getProfessor()),
                DTOMapper.toSeatDTOs(savedCourse.getSeats())
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

        return new ResponseCourseDTO(
                courseWithStudents.getName(),
                DTOMapper.toProfessorDTO(courseWithStudents.getProfessor()),
                DTOMapper.toSeatDTOs(courseWithStudents.getSeats())
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

    public ResponseSeatDTO setMarkToStudentInCourse(Long courseId, Long studentId, Double mark){

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(courseOpt.isEmpty()){
            throw new IllegalArgumentException("No se encontró el curso con ID: " + courseId);
        }

        Course course = courseOpt.get();
        Optional<Seat> seatOpt = course.getSeats().stream()
                .filter(seat -> seat.getStudent().getId().equals(studentId))
                .findFirst();
        
        if(seatOpt.isEmpty()){
            throw new IllegalArgumentException("El estudiante con ID: " + studentId + " no está inscrito en el curso con ID: " + courseId);
        }
        
        Seat seat = seatOpt.get();
        seat.setMark(mark);
        courseRepository.save(course);

        return new ResponseSeatDTO(
                seat.getYear(),
                DTOMapper.toStudentDTO(seat.getStudent()),
                seat.getMark()
        );
    }

}
