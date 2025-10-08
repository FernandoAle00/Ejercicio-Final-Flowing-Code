package com.example.ejerciciofinal.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student extends Person {

    @Column(name = "student_number", nullable = false, unique = true)
    private UUID studentNumber;

    @Column(name = "avg_mark", nullable = false)
    private Double avgMark;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats = new HashSet<>();

    public Student(String name, String phone, String email, Address address, Set<Seat> seats) {
        super(name, phone, email, address);
        this.studentNumber = UUID.randomUUID(); // Generar UUID automáticamente
        this.seats = seats;
        this.avgMark = this.calculateAvgMark();
    }

    public UUID getStudentNumber() {
        return studentNumber;
    }

    public Double getAvgMark() {
        return avgMark;
    }

    public void setAvgMark(Double avgMark) {
        this.avgMark = avgMark;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
        this.avgMark = this.calculateAvgMark();
    }

    protected Student() { // To keep Hibernate happy
    }

    public Double calculateAvgMark() {
        if (this.seats.isEmpty()) {
            return 0.0;
        }
        
        // Filtrar solo los seats que tienen nota (mark != null)
        long seatsWithMark = this.seats.stream()
            .filter(seat -> seat.getMark() != null)
            .count();
        
        if (seatsWithMark == 0) {
            return 0.0;
        }
        
        Double total = this.seats.stream()
            .filter(seat -> seat.getMark() != null)
            .mapToDouble(Seat::getMark)
            .sum();
        
        return total / seatsWithMark;
    }
}
