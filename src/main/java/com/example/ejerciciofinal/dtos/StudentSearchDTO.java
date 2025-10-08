package com.example.ejerciciofinal.dtos;

/*
 * DTO ligero para la búsqueda y filtrado de estudiantes al momento
 * de asignar un curso, ya que no necesito todos los destalles
 * simplemente con id, número de estudiante y nombre es suficiente
 */
public class StudentSearchDTO {

    private Long id;
    private String studentNumber; // transformo de UUID a string para simplificar
    private String name;

    public StudentSearchDTO(Long id, String name, String studentNumber) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * Método para generar el label del combobox
     * formato será: "Id: 1 - Juan Perez - #1234"
     */
    public String getDisplayLabel() {
        return String.format("ID: %d - %s - #%s", id, name, studentNumber);
    }

    /*
     * Método para verificar si el DTO coincide con el filtro de búsqueda
     * @param filter texto de filtro (case-insensitive)
     * @return true si coincide con id, nombre o número de estudiante
     */
    public boolean matchesFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        String lowerFilter = filter.toLowerCase();
        return (id.toString().contains(lowerFilter) || name.toLowerCase().contains(lowerFilter)
                || studentNumber.toLowerCase().contains(lowerFilter));
    }

    @Override
    public String toString() {
        return getDisplayLabel();
    }

}
