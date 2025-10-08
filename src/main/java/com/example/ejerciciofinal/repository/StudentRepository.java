package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.dtos.StudentSearchDTO;
import com.example.ejerciciofinal.model.Student;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(UUID studentNumber);

    Optional<Student> findByEmail(String email);

    /*
     * Creo una query optimizada para buscar a los estudiantes solo por los 3 campos de búsqueda
     * Es más eficiente que traer toda la entidad Student con todos sus atributos y relaciones
     * solo traigo toda la entidad una vez se haya seleccionado un estudiante
     */
    @Query("SELECT new com.example.ejerciciofinal.dtos.StudentSearchDTO(s.id, s.name, CAST(s.studentNumber AS string)) "
            + "FROM Student s "
            + "ORDER BY s.name ASC")
    List<StudentSearchDTO> findAllStudentsForSearch();

    /*
     * Buscar un estudiante por ID con carga eager de seats, address y courses
     * evita lazyinitializationexception al acceder a las colecciones fuera de la transacción
     */
    @Override
    @EntityGraph(attributePaths = {"seats", "seats.course", "address"})
    Optional<Student> findById(Long id);
}
