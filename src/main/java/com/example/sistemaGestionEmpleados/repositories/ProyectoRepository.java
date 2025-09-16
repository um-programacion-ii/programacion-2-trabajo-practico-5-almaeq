package com.example.sistemaGestionEmpleados.repositories;

import com.example.sistemaGestionEmpleados.models.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByFechaFinAfter(LocalDate fechaFin);
    @Query("SELECT p FROM Proyecto p LEFT JOIN FETCH p.empleados WHERE p.id = :id")
    Optional<Proyecto> findByIdWithEmpleados(@Param("id") Long id);
}
