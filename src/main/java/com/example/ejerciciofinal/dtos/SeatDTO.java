package com.example.ejerciciofinal.dtos;

import java.time.LocalDate;

/**
 * DTO simplificado para Seat - evita referencias circulares Solo contiene IDs y
 * nombres en lugar de objetos completos
 */
public class SeatDTO {

    private Long id;
    private LocalDate year;
    private Double mark;

    // Referencias por ID en lugar de objetos completos
    private Long studentId;
    private String studentName;

    private Long courseId;
    private String courseName;

    public SeatDTO() {
    }

    /**
     * Constructor para crear SeatDTO vacío (cupo disponible sin asignar)
     * Útil cuando se crean cupos por cantidad
     */
    public SeatDTO(LocalDate year) {
        this.year = year;
        this.mark = null;
        this.studentId = null;
        this.studentName = null;
        this.courseId = null;
        this.courseName = null;
    }

    public SeatDTO(Long id, LocalDate year, Double mark, Long studentId, String studentName,
            Long courseId, String courseName) {
        this.id = id;
        this.year = year;
        this.mark = mark;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
