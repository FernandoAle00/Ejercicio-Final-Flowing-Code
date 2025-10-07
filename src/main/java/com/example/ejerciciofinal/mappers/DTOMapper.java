package com.example.ejerciciofinal.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.ejerciciofinal.dtos.AddressDTO;
import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.StudentDTO;
import com.example.ejerciciofinal.dtos.SeatDTO;
import com.example.ejerciciofinal.dtos.StudentDetailDTO;
import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Seat;
import com.example.ejerciciofinal.model.Student;

/**
 * Clase helper para convertir entidades a DTOs sin referencias circulares
 */
public class DTOMapper {

    /**
     * Convierte un Seat a SeatDTO (sin objetos anidados)
     */
    public static SeatDTO toSeatDTO(Seat seat) {
        if (seat == null) return null;
        
        return new SeatDTO(
            seat.getId(),
            seat.getYear(),
            seat.getMark(),
            seat.getStudent() != null ? seat.getStudent().getId() : null,
            seat.getStudent() != null ? seat.getStudent().getName() : null,
            seat.getCourse() != null ? seat.getCourse().getId() : null,
            seat.getCourse() != null ? seat.getCourse().getName() : null
        );
    }

    /**
     * Convierte un Set de Seats a Set de SeatDTOs
     */
    public static Set<SeatDTO> toSeatDTOs(Set<Seat> seats) {
        if (seats == null) return null;
        
        return seats.stream()
            .map(DTOMapper::toSeatDTO)
            .collect(Collectors.toSet());
    }

    /**
     * Convierte un Student a StudentDTO (sin seats para evitar ciclos)
     * avgMark se obtiene directamente del modelo (calculado desde Seats)
     */
    public static StudentDTO toStudentDTO(Student student) {
        if (student == null) return null;
        
        return new StudentDTO(
            student.getId(),
            student.getName(),
            student.getPhone(),
            student.getEmail(),
            toAddressDTO(student.getAddress())
        );
    }

    /**
     * Convierte un Student a StudentDetailDTO (CON enrollments)
     */
    public static StudentDetailDTO toStudentDetailDTO(Student student) {
        if (student == null) return null;
        
        return new StudentDetailDTO(
            student.getId(),
            student.getName(),
            student.getPhone(),
            student.getEmail(),
            student.getStudentNumber() != null ? student.getStudentNumber().toString() : null,
            student.getAvgMark(),
            toAddressDTO(student.getAddress()),
            toSeatDTOs(student.getSeats())
        );
    }

    /**
     * Convierte un Professor a ProfessorDTO
     */
    public static ProfessorDTO toProfessorDTO(Professor professor) {
        if (professor == null) return null;
        
        return new CreateUserDTO.ProfessorDTO(
            professor.getId(),
            professor.getName(),
            professor.getPhone(),
            professor.getEmail(),
            toAddressDTO(professor.getAddress()),
            professor.getSalary()
        );
    }

    public static List<ProfessorDTO> toProfessorDTO(List<Professor> professors){
        if (professors == null) return null;

        return professors.stream()
                .map(DTOMapper::toProfessorDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un Address a AddressDTO
     */
    public static AddressDTO toAddressDTO(com.example.ejerciciofinal.model.Address address) {
        if (address == null) return null;
        
        return new AddressDTO(
            address.getStreet(),
            address.getCity(),
            address.getState(),
            address.getCountry()
        );
    }

    /**
     * Convierte un Course a CourseDTO (con seats simplificados)
     */
    public static CourseDTO toCourseDTO(Course course) {
        if (course == null) return null;
        
        return new CourseDTO(
            course.getId(),
            course.getName(),
            course.getProfessor() != null ? course.getProfessor().getId() : null,
            course.getProfessor() != null ? course.getProfessor().getName() : null,
            toSeatDTOs(course.getSeats())
        );
    }

    public static List<CourseDTO> toCourseDTOs(List<Course> courses){
        if (courses == null) return null;

        return courses.stream()
                .map(DTOMapper::toCourseDTO)
                .collect(Collectors.toList());
    }
}
