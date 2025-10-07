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
import com.example.ejerciciofinal.mappers.DTOMapper;
import com.example.ejerciciofinal.model.Address;
import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.model.User;
import com.example.ejerciciofinal.repository.PersonRepository;
import com.example.ejerciciofinal.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public ResponseUserDTO createUser(CreateUserDTO userDTO) {

        validateCreateUserDTO(userDTO);

        if (userRepository.existsByUserName(userDTO.getUserName())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Person person = null;
        if (userDTO.getPerson() != null) {
            person = createPersonFromDTO(userDTO.getPerson());
        }

        // Crear User
        User user = new User(
                userDTO.getUserName(),
                userDTO.getPassword(), // En un caso real, la contraseña debería ser hasheada
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
                return new Professor(
                        personDTO.getName(),
                        personDTO.getPhone(),
                        personDTO.getEmail(),
                        getAddressFromAddressDTO(personDTO.getAddress()),
                        professorDTO.getSalary() != null ? professorDTO.getSalary() : 0.0
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

    public Person getPersonById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario con ID: " + id)).getPerson();
    }

    public List<ProfessorDTO> getAllProfessors() {
        return DTOMapper.toProfessorDTO(personRepository.findAllProfessors());
    }

    /**
     * Obtiene todos los usuarios de tipo Person paginados
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
     * @return Total de registros Person
     */
    @Transactional(readOnly = true)
    public long countPersons() {
        return personRepository.count();
    }

}
