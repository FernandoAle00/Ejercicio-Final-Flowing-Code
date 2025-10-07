package com.example.ejerciciofinal.repository;

import com.example.ejerciciofinal.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    /**
     * Obtiene todas las personas paginadas con Address cargado (EAGER)
     * Evita LazyInitializationException al acceder a address.getCity()
     */
    @EntityGraph(attributePaths = {"address"})
    Page<Person> findAll(Pageable pageable);
}
