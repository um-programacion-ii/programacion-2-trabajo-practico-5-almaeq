package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.models.Proyecto;
import com.example.sistemaGestionEmpleados.services.ProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida con éxito")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proyecto encontrado",
                    content = @Content(schema = @Schema(implementation = Proyecto.class))),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado",
                    content = @Content)
    })
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
    @ApiResponse(responseCode = "201", description = "Proyecto creado exitosamente")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proyecto actualizado"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2.6.0", description = "Proyecto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
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
    @ApiResponse(responseCode = "200", description = "Búsqueda de proyectos activos completada")
    @GetMapping("/activos")
    public List<Proyecto> obtenerProyectosActivos() {
        return proyectoService.buscarPorProyectosActivos();
    }
}
