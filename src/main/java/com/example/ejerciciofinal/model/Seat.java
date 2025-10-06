package com.example.ejerciciofinal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", nullable = false)
    private LocalDate year;

    @Column(name = "mark", nullable = false)
    private Double mark;

    public Long getId() {
        return id;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Seat(LocalDate year, Double mark, Student student, Course course) {
        this.year = year;
        this.mark = mark;
        this.student = student;
        this.course = course;
    }

    protected Seat() { // To keep Hibernate happy
    }

}
