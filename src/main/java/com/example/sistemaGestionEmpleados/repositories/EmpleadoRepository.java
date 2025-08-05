package com.example.sistemaGestionEmpleados.repositories;

import com.example.sistemaGestionEmpleados.models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
