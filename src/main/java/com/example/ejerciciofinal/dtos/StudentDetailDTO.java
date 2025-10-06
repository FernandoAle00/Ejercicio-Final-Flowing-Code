package com.example.ejerciciofinal.dtos;

import java.util.Set;

/**
 * DTO completo para mostrar detalles de un estudiante con sus inscripciones Se
 * usa para respuestas GET, no para creación
 */
public class StudentDetailDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String studentNumber;
    private Double avgMark;
    private AddressDTO address;

    // Solo información resumida de los cursos (sin objetos anidados completos)
    private Set<SeatDTO> enrollments;

    public StudentDetailDTO() {
    }

    public StudentDetailDTO(Long id, String name, String phone, String email,
            String studentNumber, Double avgMark, AddressDTO address,
            Set<SeatDTO> enrollments) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.studentNumber = studentNumber;
        this.avgMark = avgMark;
        this.address = address;
        this.enrollments = enrollments;
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

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Double getAvgMark() {
        return avgMark;
    }

    public void setAvgMark(Double avgMark) {
        this.avgMark = avgMark;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public Set<SeatDTO> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<SeatDTO> enrollments) {
        this.enrollments = enrollments;
    }
}
