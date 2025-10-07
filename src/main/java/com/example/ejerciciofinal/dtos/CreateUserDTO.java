package com.example.ejerciciofinal.dtos;

import com.example.ejerciciofinal.model.Role;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * DTO para la creación de usuarios
 */
public class CreateUserDTO {

    private String userName;
    private String password;
    private Role role;
    private PersonDTO person = null;

    public CreateUserDTO() {
    }

    public CreateUserDTO(String userName, String password, Role role, PersonDTO person) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.person = person;
    }

    // Getters y Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    // ===== CLASES INTERNAS =====

    /**
     * DTO base para Person (polimórfico)
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = StudentDTO.class, name = "student"),
        @JsonSubTypes.Type(value = ProfessorDTO.class, name = "professor")
    })
    public static abstract class PersonDTO {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private AddressDTO address;

        public PersonDTO() {
        }

        public PersonDTO(Long id, String name, String phone, String email, AddressDTO address) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        // Getters y Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public AddressDTO getAddress() {
            return address;
        }

        public void setAddress(AddressDTO address) {
            this.address = address;
        }
    }

    /**
     * DTO para Student - SIN Set<SeatDTO> para evitar referencias circulares
     * studentNumber se genera automáticamente como UUID
     * avgMark se calcula automáticamente desde los Seats asignados
     */
    public static class StudentDTO extends PersonDTO {

        public StudentDTO() {
        }

        public StudentDTO(Long id, String name, String phone, String email, AddressDTO address) {
            super(id, name, phone, email, address);
        }
    }

    /**
     * DTO para Professor
     */
    public static class ProfessorDTO extends PersonDTO {
        private Double salary;

        public ProfessorDTO() {
        }

        public ProfessorDTO(Long id, String name, String phone, String email, AddressDTO address, 
                           Double salary) {
            super(id, name, phone, email, address);
            this.salary = salary;
        }

        public Double getSalary() {
            return salary;
        }

        public void setSalary(Double salary) {
            this.salary = salary;
        }
    }
}
