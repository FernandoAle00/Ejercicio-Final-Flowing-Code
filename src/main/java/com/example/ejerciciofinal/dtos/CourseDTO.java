package com.example.ejerciciofinal.dtos;

import java.util.Set;

/**
 * DTO simplificado para Course - evita referencias circulares Solo contiene IDs
 * y nombres del profesor en lugar del objeto completo
 */
public class CourseDTO {

    private Long id;
    private String name;

    // Solo referencia al profesor, no objeto completo
    private Long professorId;
    private String professorName;

    // Lista simplificada de seats (sin datos anidados)
    private Set<SeatDTO> seats;

    public CourseDTO() {
    }

    public CourseDTO(Long id, String name, Long professorId, String professorName, Set<SeatDTO> seats) {
        this.id = id;
        this.name = name;
        this.professorId = professorId;
        this.professorName = professorName;
        this.seats = seats;
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

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public Set<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(Set<SeatDTO> seats) {
        this.seats = seats;
    }
}
