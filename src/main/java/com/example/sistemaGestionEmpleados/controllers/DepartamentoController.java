package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.services.DepartamentoService;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@Validated
public class DepartamentoController {

    private DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public List<Departamento> obtenerTodos() {
        return departamentoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Departamento obtenerPorId(@PathVariable Long id) {
        return departamentoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Departamento crear( @RequestBody Departamento departamento) {
        return departamentoService.guardar(departamento);
    }

    @PutMapping("/{id}")
    public Departamento actualizar(@PathVariable Long id, @RequestBody Departamento departamento) {
        return departamentoService.actualizar(id, departamento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        departamentoService.eliminar(id);
    }

}
