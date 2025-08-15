package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.models.Proyecto;
import com.example.sistemaGestionEmpleados.services.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<Proyecto> obtenerTodos() {
        return proyectoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Proyecto obtenerPorId(@PathVariable Long id) {
        return proyectoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proyecto crear( @RequestBody Proyecto proyecto) {
        return proyectoService.guardar(proyecto);
    }

    @PutMapping("/{id}")
    public Proyecto actualizar(@PathVariable Long id, @RequestBody Proyecto proyecto) {
        return proyectoService.actualizar(id, proyecto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
    }

    @GetMapping("/activos")
    public List<Proyecto> obtenerPorDepartamento() {
        return proyectoService.buscarPorProyectosActivos();
    }
}
