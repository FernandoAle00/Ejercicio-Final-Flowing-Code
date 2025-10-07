package com.example.ejerciciofinal.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats = new HashSet<>();
    
    protected Course() { // To keep Hibernate happy
    }

    public Course(String name, Professor professor, Set<Seat> seats) {
        this.name = name;
        this.professor = professor;
        this.seats = seats;
    }

    /**
     * Constructor para crear un curso con una cantidad específica de cupos vacíos
     * @param name Nombre del curso
     * @param professor Profesor asignado
     * @param numberOfSeats Cantidad de cupos a crear
     */
    public Course(String name, Professor professor, int numberOfSeats) {
        this.name = name;
        this.professor = professor;
        this.seats = new HashSet<>();
        
        // Crear cupos vacíos (sin estudiantes asignados)
        for (int i = 0; i < numberOfSeats; i++) {
            Seat seat = new Seat(
                LocalDate.now(), // Año actual
                null,            // Sin nota inicial
                null,            // Sin estudiante asignado
                this             // Curso actual
            );
            this.seats.add(seat);
        }
    }
}
