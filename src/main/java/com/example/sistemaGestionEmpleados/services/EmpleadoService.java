package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.EmpleadoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Empleado;

import java.math.BigDecimal;
import java.util.List;

public interface EmpleadoService {
    Empleado guardar(Empleado empleado);
    Empleado buscarPorId(Long id);
    List<Empleado> buscarPorDepartamento(String nombreDepartamento);
    List<Empleado> buscarPorRangoSalario(BigDecimal salarioMin, BigDecimal salarioMax);
    BigDecimal obtenerSalarioPromedioPorDepartamento(Long departamentoId);
    List<Empleado> obtenerTodos();
    Empleado actualizar(Long id, Empleado empleado) throws EmpleadoNoEncontradoException;
    void eliminar(Long id);
}
