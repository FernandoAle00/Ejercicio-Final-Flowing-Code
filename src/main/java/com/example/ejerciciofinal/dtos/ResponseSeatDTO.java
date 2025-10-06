package com.example.ejerciciofinal.dtos;

import java.time.LocalDate;

import com.example.ejerciciofinal.dtos.CreateUserDTO.StudentDTO;

public class ResponseSeatDTO {

    private LocalDate year;
    private StudentDTO student;
    private Double mark;

    public ResponseSeatDTO() {
    }

    public ResponseSeatDTO(LocalDate year, StudentDTO student, Double mark) {
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

    public StudentDTO getStudent() {
        return student;
    }
    public void setStudent(StudentDTO student) {
        this.student = student;
    }
    
}
