package com.example.ejerciciofinal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ejerciciofinal.dtos.AddressDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.PersonDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.StudentDTO;
import com.example.ejerciciofinal.dtos.ResponseUserDTO;
import com.example.ejerciciofinal.model.Address;
import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.model.User;
import com.example.ejerciciofinal.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
                return new Student(
                        personDTO.getName(),
                        personDTO.getPhone(),
                        personDTO.getEmail(),
                        getAddressFromAddressDTO(personDTO.getAddress()),
                        studentDTO.getAvgMark()
                );
            }
            case ProfessorDTO professorDTO -> {
                return new Professor(
                        personDTO.getName(),
                        personDTO.getPhone(),
                        personDTO.getEmail(),
                        getAddressFromAddressDTO(personDTO.getAddress()),
                        professorDTO.getSalary()
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

}
