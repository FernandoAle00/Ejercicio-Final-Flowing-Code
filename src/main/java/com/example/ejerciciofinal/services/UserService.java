package com.example.ejerciciofinal.services;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejerciciofinal.dtos.AddressDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.PersonDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.StudentDTO;
import com.example.ejerciciofinal.dtos.ResponseUserDTO;
import com.example.ejerciciofinal.dtos.StudentSearchDTO;
import com.example.ejerciciofinal.mappers.DTOMapper;
import com.example.ejerciciofinal.model.Address;
import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.model.User;
import com.example.ejerciciofinal.repository.PersonRepository;
import com.example.ejerciciofinal.repository.ProfessorRepository;
import com.example.ejerciciofinal.repository.StudentRepository;
import com.example.ejerciciofinal.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional
    public ResponseUserDTO createUser(CreateUserDTO userDTO) {

        validateCreateUserDTO(userDTO);

        if (userRepository.existsByUserName(userDTO.getUserName())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Person person = null;
        
        // Solo crear Person si NO es ADMIN
        if (userDTO.getPerson() != null) {
            person = createPersonFromDTO(userDTO.getPerson());
            
            // Validar que el email no esté duplicado
            if (personRepository.existsByEmail(person.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        }

        // Crear User con contraseña en texto plano (en producción usar BCrypt)
        User user = new User(
                userDTO.getUserName(),
                userDTO.getPassword(),
                userDTO.getRole(),
                person
        );

        User savedUser = userRepository.save(user);

        return new ResponseUserDTO(
                savedUser.getUserName(),
                savedUser.getRole(),
                userDTO.getPerson() != null ? userDTO.getPerson() : null
        );
    }

    private void validateCreateUserDTO(CreateUserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("El objeto userDTO no puede ser nulo");
        }
        if (userDTO.getUserName() == null || userDTO.getUserName().isEmpty()) {
            throw new IllegalArgumentException("El userName no puede estar vacío");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("El password no puede estar vacío");
        }
        if (userDTO.getRole() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }

    private Person createPersonFromDTO(PersonDTO personDTO) {
        switch (personDTO) {
            case null -> {
                return null;
            }
            case StudentDTO studentDTO -> {
                // Student se crea sin seats inicialmente
                // Los seats se agregarán después mediante SeatService
                return new Student(
                        personDTO.getName(),
                        personDTO.getPhone(),
                        personDTO.getEmail(),
                        getAddressFromAddressDTO(personDTO.getAddress()),
                        new HashSet<>() // Inicialmente vacío
                );
            }
            case ProfessorDTO professorDTO -> {
                Double salary = professorDTO.getSalary() != null ? professorDTO.getSalary() : 0.0;
                return new Professor(
                        personDTO.getName(),
                        personDTO.getPhone(),
                        personDTO.getEmail(),
                        getAddressFromAddressDTO(personDTO.getAddress()),
                        salary
                );
            }
            default -> {
            }
        }
        return null; // En el caso de ADMIN, no se establece un Person al User
    }

    private Address getAddressFromAddressDTO(AddressDTO address) {
        if (address == null) {
            return null;
        }
        return new Address(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry()
        );
    }

    public Person getPersonById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id)).getPerson();
    }

    public List<ProfessorDTO> getAllProfessors() {
        return DTOMapper.toProfessorDTO(personRepository.findAllProfessors());
    }

    /**
     * Obtiene todos los usuarios de tipo Person paginados
     *
     * @param pageIndex Índice de la página (comienza en 0)
     * @param pageSize Cantidad de registros por página
     * @return Page<Person> con los usuarios paginados
     */
    @Transactional(readOnly = true)
    public Page<Person> findAllPersonsPaginated(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").ascending());
        return personRepository.findAll(pageable);
    }

    /**
     * Cuenta el total de personas registradas
     *
     * @return Total de registros Person
     */
    @Transactional(readOnly = true)
    public long countPersons() {
        return personRepository.count();
    }

    /*
     * Obtiene todosl os estudiantes para búsqueda (solo id, nombre y número de estudiante)
     * query optimizada que no trae todos los campos student
     * @return Lista de StudentSearchDTO con 3 campos, id, name, studentNumber
     */
    @Transactional(readOnly = true)
    public List<StudentSearchDTO> getAllStudentsForSearch() {
        return studentRepository.findAllStudentsForSearch();
    }

    /*
     * Obtiene un estudiante completo por ID (con todos sus datos)
     * @param id del estudiante
     * @return Student completo
     */
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + id)
                );
    }

    /*
     * Chequea la condición de que un estudiante puede inscribirse a un curso
     * @param studentId ID del estudiante, courseId ID del curso
     * @return true si puede asignarse, false si no
     */
    @Transactional(readOnly = true)
    public boolean canAssignStudentToCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + studentId));
        return student.getSeats().stream()
                .noneMatch(seat -> seat.getCourse().getId().equals(courseId));
    }

    /*
     * Obtiene un profesor completo por ID (con todos sus datos y cursos)
     * @param id del profesor
     * @return Professor completo
     */
    @Transactional(readOnly = true)
    public Professor getProfessorById(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el profesor con ID: " + id));
    }
}
