package com.example.ejerciciofinal.dtos;

import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Role;

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public class PersonDTO {

        private Long id;
        private String name;
        private String phone;
        private String email;
        private AddressDTO address;

        public PersonDTO() {
        }

        public PersonDTO(String name, String phone, String email, AddressDTO address) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public void setAddress(AddressDTO address) {
            this.address = address;
        }

        public AddressDTO getAddress() {
            return address;
        }
    }

    public class StudentDTO extends PersonDTO {

        private Double avgMark;

        public StudentDTO(String name, String phone, String email, AddressDTO address, Double avgMark) {
            super(name, phone, email, address);
            this.avgMark = avgMark;
        }

        public Double getAvgMark() {
            return avgMark;
        }

        public void setAvgMark(Double avgMark) {
            this.avgMark = avgMark;
        }

    }

    public class ProfessorDTO extends PersonDTO {

        private Double salary;

        public ProfessorDTO(String name, String phone, String email, AddressDTO address, Double salary) {
            super(name, phone, email, address);
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
