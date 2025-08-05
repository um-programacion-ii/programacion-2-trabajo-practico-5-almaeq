package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.ProyectoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Proyecto;

import java.util.List;

public interface ProyectoService {
    Proyecto guardar(Proyecto proyecto);
    Proyecto buscarPorId(Long id);
    List<Proyecto> obtenerTodos();
    Proyecto actualizar(Long id, Proyecto proyecto) throws ProyectoNoEncontradoException;
    List<Proyecto> buscarPorProyectosActivos();
    void eliminar(Long id);
}
