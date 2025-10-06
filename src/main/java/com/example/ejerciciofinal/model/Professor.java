package com.example.ejerciciofinal.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "professors")
public class Professor extends Person {

    @Column(name = "salary", nullable = false)
    private Double salary;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Course> courses = new HashSet<>();

    public Professor(String name, String phone, String email, Address address, Double salary) {
        super(name, phone, email, address);
        this.salary = salary;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    protected Professor() { // To keep Hibernate happy
    }
}
