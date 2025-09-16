package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.DepartamentoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Departamento;

import java.util.List;

public interface DepartamentoService {
    Departamento guardar(Departamento departamento);
    Departamento buscarPorId(Long id);
    List<Departamento> obtenerTodos();
    Departamento actualizar(Long id, Departamento departamento) throws DepartamentoNoEncontradoException;
    void eliminar(Long id);
}
