package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.models.Proyecto;
import com.example.sistemaGestionEmpleados.services.ProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "API de Proyectos", description = "Operaciones CRUD y consultas para la gestión de proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    /**
     * Javadoc: Obtiene una lista de todos los proyectos registrados.
     * @return Lista de objetos Proyecto.
     */
    @Operation(summary = "Obtener todos los proyectos", description = "Devuelve una lista con todos los proyectos existentes.")
    @GetMapping
    public List<Proyecto> obtenerTodos() {
        return proyectoService.obtenerTodos();
    }

    /**
     * Javadoc: Busca un proyecto específico por su ID.
     * @param id El ID único del proyecto a buscar.
     * @return El objeto Proyecto correspondiente al ID.
     */
    @Operation(summary = "Obtener un proyecto por su ID")
    @GetMapping("/{id}")
    public Proyecto obtenerPorId(
            @Parameter(description = "ID del proyecto a obtener", required = true) @PathVariable Long id
    ) {
        return proyectoService.buscarPorId(id);
    }

    /**
     * Javadoc: Crea y guarda un nuevo proyecto.
     * @param proyecto El objeto Proyecto a crear, proporcionado en el cuerpo de la solicitud.
     * @return El proyecto guardado, incluyendo el ID asignado por la base de datos.
     */
    @Operation(summary = "Crear un nuevo proyecto")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proyecto crear(@RequestBody Proyecto proyecto) {
        return proyectoService.guardar(proyecto);
    }

    /**
     * Javadoc: Actualiza la información de un proyecto existente.
     * @param id El ID del proyecto que se va a actualizar.
     * @param proyecto El objeto Proyecto con los datos actualizados.
     * @return El proyecto con su información modificada.
     */
    @Operation(summary = "Actualizar un proyecto existente")
    @PutMapping("/{id}")
    public Proyecto actualizar(
            @Parameter(description = "ID del proyecto a actualizar", required = true) @PathVariable Long id,
            @RequestBody Proyecto proyecto
    ) {
        return proyectoService.actualizar(id, proyecto);
    }

    /**
     * Javadoc: Elimina un proyecto de la base de datos.
     * @param id El ID del proyecto a eliminar.
     */
    @Operation(summary = "Eliminar un proyecto por su ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "ID del proyecto a eliminar", required = true) @PathVariable Long id
    ) {
        proyectoService.eliminar(id);
    }

    /**
     * Javadoc: Obtiene una lista de todos los proyectos que se consideran activos
     * (aquellos cuya fecha de finalización es posterior a la fecha actual).
     * @return Lista de proyectos activos.
     */
    @Operation(summary = "Obtener todos los proyectos activos", description = "Devuelve una lista de proyectos cuya fecha de finalización es posterior a la fecha actual.")
    @GetMapping("/activos")
    public List<Proyecto> obtenerProyectosActivos() {
        return proyectoService.buscarPorProyectosActivos();
    }

    /**
     * Javadoc: Asigna o reemplaza la lista de empleados asociados a un proyecto específico.
     * Si el proyecto ya tenía empleados, estos serán desvinculados antes de asignar los nuevos.
     *
     * @param proyectoId  El ID del proyecto al que se le asignarán los empleados.
     * @param empleadoIds Un conjunto de IDs de los empleados que serán asignados al proyecto.
     * @return El proyecto actualizado con la nueva lista de empleados.
     */
    @Operation(summary = "Asignar empleados a un proyecto", description = "Asocia una lista de empleados a un proyecto existente usando sus IDs.")
    @PutMapping("/{proyectoId}/asignar-empleados")
    public Proyecto asignarEmpleados(
            @Parameter(description = "ID del proyecto", required = true) @PathVariable Long proyectoId,
            @RequestBody Set<Long> empleadoIds
    ) {
        return proyectoService.asignarEmpleadosAProyecto(proyectoId, empleadoIds);
    }
}
