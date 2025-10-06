package com.example.ejerciciofinal.dtos;

import java.time.LocalDate;

public class ResponseSeatDTO {

    private LocalDate year;
    private CreateUserDTO.StudentDTO student;
    private Double mark;

    public ResponseSeatDTO() {
    }

    public ResponseSeatDTO(LocalDate year, CreateUserDTO.StudentDTO student, Double mark) {
        this.year = year;
        this.student = student;
        this.mark = mark;
    }

    public LocalDate getYear() {
        return year;
    }
    
    public void setYear(LocalDate year) {
        this.year = year;
    }

    public Double getMark() {
        return mark;
    }
    
    public void setMark(Double mark) {
        this.mark = mark;
    }

    public CreateUserDTO.StudentDTO getStudent() {
        return student;
    }
    
    public void setStudent(CreateUserDTO.StudentDTO student) {
        this.student = student;
    }

}
