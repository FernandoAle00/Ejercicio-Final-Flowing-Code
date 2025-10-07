package com.example.ejerciciofinal.dtos;

import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;

import java.util.Set;
import java.util.HashSet;

public class CreateCourseDTO {

    private String name;
    private ProfessorDTO professor;
    private Set<SeatDTO> seats = new HashSet<>();
    private Integer seatsAmmount; // Cantidad de cupos a crear

    public CreateCourseDTO() {
    }

    public CreateCourseDTO(String name, ProfessorDTO professor, Set<SeatDTO> seats) {
        this.name = name;
        this.professor = professor;
        this.seats = seats;
    }

    /**
     * Constructor para crear un curso con una cantidad específica de cupos vacíos
     * @param name Nombre del curso
     * @param professor Profesor asignado
     * @param seatsAmmount Cantidad de cupos a generar
     */
    public CreateCourseDTO(String name, ProfessorDTO professor, int seatsAmmount) {
        this.name = name;
        this.professor = professor;
        this.seatsAmmount = seatsAmmount;
        // Los SeatDTO se generarán en el servicio
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

    public Set<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(Set<SeatDTO> seats) {
        this.seats = seats;
    }

    public Integer getSeatsAmmount() {
        return seatsAmmount;
    }

    public void setSeatsAmmount(Integer seatsAmmount) {
        this.seatsAmmount = seatsAmmount;
    }
}
