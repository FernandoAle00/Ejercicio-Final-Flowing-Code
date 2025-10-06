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
        this.seats = seats;
        this.avgMark = this.calculateAvgMark(seats);
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
        this.avgMark = this.calculateAvgMark(seats);
    }

    protected Student() { // To keep Hibernate happy
    }

    private Double calculateAvgMark(Set<Seat> seats) {
        if (seats.isEmpty()) {
            return 0.0;
        } else {
            Double total = 0.0;
            for (Seat seat : seats) {
                total += seat.getMark();
            }
            return total / seats.size();
        }
    }
}
