package com.example.ejerciciofinal.services;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.dtos.CreateCourseDTO;
import com.example.ejerciciofinal.dtos.ResponseCourseDTO;
import com.example.ejerciciofinal.dtos.ResponseSeatDTO;
import com.example.ejerciciofinal.dtos.SeatDTO;
import com.example.ejerciciofinal.mappers.DTOMapper;
import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Seat;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.repository.CourseRepository;
import com.example.ejerciciofinal.repository.ProfessorRepository;
import com.example.ejerciciofinal.repository.StudentRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public ResponseCourseDTO createCourse(CreateCourseDTO courseDTO) {

        validateCourseDTO(courseDTO);

        // Obtener el profesor directamente del repositorio de profesores
        Professor professor = professorRepository.findById(courseDTO.getProfessor().getId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el profesor con ID: " + courseDTO.getProfessor().getId()));
        
        Course course;

        // Opción 1: Si se especificó cantidad de cupos, crear curso con esa cantidad
        if (courseDTO.getSeatsAmmount() != null && courseDTO.getSeatsAmmount() > 0) {
            course = new Course(
                courseDTO.getName(),
                professor,
                courseDTO.getSeatsAmmount()
            );
        }
        // Opción 2: Si se proporcionaron SeatDTOs, convertirlos a Seats
        else if (courseDTO.getSeats() != null && !courseDTO.getSeats().isEmpty()) {
            Set<Seat> seats = new HashSet<>();
            course = new Course(courseDTO.getName(), professor, new HashSet<>());
            
            for (SeatDTO seatDTO : courseDTO.getSeats()) {
                Seat seat = new Seat(
                    seatDTO.getYear() != null ? seatDTO.getYear() : LocalDate.now(),
                    seatDTO.getMark(),
                    seatDTO.getStudentId() != null ? (Student) userService.getPersonById(seatDTO.getStudentId()) : null,
                    course
                );
                seats.add(seat);
            }
            course.setSeats(seats);
        }
        // Opción 3: Crear curso sin cupos
        else {
            course = new Course(
                courseDTO.getName(),
                professor,
                new HashSet<>()
            );
        }

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

    @Transactional
    public ResponseSeatDTO setMarkToStudentInCourse(Long courseId, Long studentId, Double mark){

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(courseOpt.isEmpty()){
            throw new IllegalArgumentException("No se encontró el curso con ID: " + courseId);
        }

        Course course = courseOpt.get();
        Optional<Seat> seatOpt = course.getSeats().stream()
                .filter(seat -> seat.getStudent() != null && seat.getStudent().getId().equals(studentId))
                .findFirst();
        
        if(seatOpt.isEmpty()){
            throw new IllegalArgumentException("El estudiante con ID: " + studentId + " no está inscrito en el curso con ID: " + courseId);
        }
        
        Seat seat = seatOpt.get();
        seat.setMark(mark);
        
        // Recalcular y actualizar el promedio del estudiante
        Student student = seat.getStudent();
        Double newAvgMark = student.calculateAvgMark();
        student.setAvgMark(newAvgMark);
        
        courseRepository.save(course);

        return new ResponseSeatDTO(
                seat.getYear(),
                DTOMapper.toStudentDTO(seat.getStudent()),
                seat.getMark()
        );
    }

    /*
     * Obtiene todos los cursos de tipo Course paginados
     * @param pageIndex índice de la página (comienza en 0)
     * @param pageSize cantidad de registros por página
     *  return Page<Course> con los cursos paginados
     */
    @Transactional(readOnly = true)
    public Page<Course> findAllCoursesPaginated(int pageIndex, int pageSize){
        Pageable pageable =  PageRequest.of(pageIndex, pageSize, Sort.by("id").ascending());
        return courseRepository.findAll(pageable);
    }

    /*  
     * Cuenta el total de cursos registrados
     * @return total de registros Course
     */
    @Transactional(readOnly = true)
    public long countCourses(){
        return courseRepository.count();
    }

    public List<CourseDTO> getAllCourses() {
        Optional<List<Course>> coursesOpt = Optional.of(courseRepository.findAll());
        if(coursesOpt.isPresent()){
            return DTOMapper.toCourseDTOs(coursesOpt.get());
        }
        else return null;
    }

    /*
     * Obtiene un curso por el ID
     */
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long courseId){
        return courseRepository.findById(courseId)
                .map(DTOMapper::toCourseDTO)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el curso con ID: " + courseId));
    }

    /*
     * Función para asignar un estudiante a un curso
     * @param studentId ID del estudiante, courseId ID del curso
     */
    @Transactional
    public void assignStudentToCourse(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el curso con ID: " + courseId));
        
        // Obtener estudiante directamente del repositorio de estudiantes (con eager loading)
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + studentId));
        
        // Verificar si el estudiante ya está asignado al curso
        boolean alreadyAssigned = course.getSeats().stream()
                .anyMatch(seat -> seat.getStudent() != null && seat.getStudent().getId().equals(studentId));
        if (alreadyAssigned) {
            throw new IllegalArgumentException("El estudiante con ID: " + studentId + " ya está asignado al curso con ID: " + courseId);
        }
        // Verificar si hay cupos disponibles
        Optional<Seat> availableSeatOpt = course.getSeats().stream()
                .filter(seat -> seat.getStudent() == null)
                .findFirst();
        if (availableSeatOpt.isEmpty()) {
            throw new IllegalArgumentException("No hay cupos disponibles en el curso con ID: " + courseId);
        }
        // Asignar el estudiante al cupo disponible
        Seat availableSeat = availableSeatOpt.get();
        availableSeat.setStudent(student);
        student.getSeats().add(availableSeat);
        courseRepository.save(course);
    }

    /*
     * Función para desinscribir un estudiante de un curso
     * @param studentId ID del estudiante, courseId ID del curso
     */
    @Transactional
    public void unassignStudentFromCourse(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el curso con ID: " + courseId));
        
        // Buscar el seat del estudiante en este curso
        Optional<Seat> studentSeatOpt = course.getSeats().stream()
                .filter(seat -> seat.getStudent() != null && seat.getStudent().getId().equals(studentId))
                .findFirst();
        
        if (studentSeatOpt.isEmpty()) {
            throw new IllegalArgumentException("El estudiante con ID: " + studentId + " no está inscrito en el curso con ID: " + courseId);
        }
        
        // Desinscribir: dejar el seat vacío (sin estudiante ni nota)
        Seat studentSeat = studentSeatOpt.get();
        Student student = studentSeat.getStudent();
        
        // Remover la relación bidireccional
        student.getSeats().remove(studentSeat);
        studentSeat.setStudent(null);
        studentSeat.setMark(null); // Limpiar la nota también
        
        courseRepository.save(course);
    }

    /*
     * Permite obtener a los students que están asignados a un curso específico
     * Retorna estudiantes con seats y seats.course cargados eager para evitar LazyInitializationException
     */
    @Transactional(readOnly = true)
    public List<Student> getStudentsInCourse(Long courseId){
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(courseOpt.isEmpty()){
            throw new IllegalArgumentException("No se encontró el curso con ID: " + courseId);
        }
        Course course = courseOpt.get();
        
        // Obtener IDs de estudiantes del curso
        List<Long> studentIds = course.getSeats().stream()
                .filter(seat -> seat.getStudent() != null)
                .map(seat -> seat.getStudent().getId())
                .toList();
        
        // Cargar estudiantes completos con eager loading de seats y seats.course
        return studentIds.stream()
                .map(id -> studentRepository.findById(id).orElse(null))
                .filter(student -> student != null)
                .toList();
    }
}
