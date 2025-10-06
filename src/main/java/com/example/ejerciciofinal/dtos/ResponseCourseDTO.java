package com.example.ejerciciofinal.dtos;

import java.util.Set;

import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.model.Seat;

public class ResponseCourseDTO {

    private String name;
    private ProfessorDTO professor;
    private Set<Seat> seats;

    public ResponseCourseDTO() {
    }

    public ResponseCourseDTO(String name, ProfessorDTO professor, Set<Seat> seats) {
        this.name = name;
        this.professor = professor;
        this.seats = seats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProfessorDTO getProfessor() {
        return professor;
    }
    public void setProfessor(ProfessorDTO professor) {
        this.professor = professor;
    }
    public Set<Seat> getSeats() {
        return seats;
    }
    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

}
