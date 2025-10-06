package com.example.ejerciciofinal.dtos;

import java.util.Set;

public class ResponseCourseDTO {

    private String name;
    private CreateUserDTO.ProfessorDTO professor;
    private Set<SeatDTO> seats;

    public ResponseCourseDTO() {
    }

    public ResponseCourseDTO(String name, CreateUserDTO.ProfessorDTO professor, Set<SeatDTO> seats) {
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

    public CreateUserDTO.ProfessorDTO getProfessor() {
        return professor;
    }
    
    public void setProfessor(CreateUserDTO.ProfessorDTO professor) {
        this.professor = professor;
    }
    
    public Set<SeatDTO> getSeats() {
        return seats;
    }
    
    public void setSeats(Set<SeatDTO> seats) {
        this.seats = seats;
    }

}
