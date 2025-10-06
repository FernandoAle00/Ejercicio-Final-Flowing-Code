package com.example.ejerciciofinal.dtos;

import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.model.Seat;

import java.util.Set;
import java.util.HashSet;

public class CreateCourseDTO {

    private String name;
    private ProfessorDTO professor;
    private Set<Seat> seats = new HashSet<>();

    public CreateCourseDTO() {
    }

    public CreateCourseDTO(String name, ProfessorDTO professor, Set<Seat> seats) {
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
