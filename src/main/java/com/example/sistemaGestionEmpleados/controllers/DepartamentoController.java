package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.services.DepartamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@Validated
@Tag(name = "API de Departamentos", description = "Operaciones CRUD para la gestión de departamentos")
public class DepartamentoController {

    private DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    /**
     * Javadoc: Obtiene una lista completa de todos los departamentos existentes.
     * @return Una lista de objetos Departamento.
     */
    @Operation(summary = "Obtener todos los departamentos", description = "Devuelve una lista con todos los departamentos.")
    @ApiResponse(responseCode = "200", description = "Lista de departamentos obtenida con éxito")
    @GetMapping
    public List<Departamento> obtenerTodos() {
        return departamentoService.obtenerTodos();
    }

    /**
     * Javadoc: Busca y devuelve un departamento específico por su ID.
     * @param id El ID único del departamento a buscar.
     * @return El objeto Departamento correspondiente al ID.
     */
    @Operation(summary = "Obtener un departamento por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento encontrado",
                    content = @Content(schema = @Schema(implementation = Departamento.class))),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public Departamento obtenerPorId(
            @Parameter(description = "ID del departamento a obtener", required = true) @PathVariable Long id
    ) {
        return departamentoService.buscarPorId(id);
    }

    /**
     * Javadoc: Crea un nuevo departamento en la base de datos.
     * @param departamento El objeto Departamento a crear, recibido en el cuerpo de la solicitud.
     * @return El objeto Departamento guardado, incluyendo su ID generado.
     */
    @Operation(summary = "Crear un nuevo departamento")
    @ApiResponse(responseCode = "201", description = "Departamento creado exitosamente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Departamento crear(@RequestBody Departamento departamento) {
        return departamentoService.guardar(departamento);
    }

    /**
     * Javadoc: Actualiza los datos de un departamento existente.
     * @param id El ID del departamento a actualizar.
     * @param departamento El objeto Departamento con los nuevos datos.
     * @return El objeto Departamento con la información actualizada.
     */
    @Operation(summary = "Actualizar un departamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento actualizado"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    @PutMapping("/{id}")
    public Departamento actualizar(
            @Parameter(description = "ID del departamento a actualizar", required = true) @PathVariable Long id,
            @RequestBody Departamento departamento
    ) {
        return departamentoService.actualizar(id, departamento);
    }

    /**
     * Javadoc: Elimina un departamento de la base de datos por su ID.
     * @param id El ID del departamento a eliminar.
     */
    @Operation(summary = "Eliminar un departamento por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2.5.0", description = "Departamento eliminado"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "ID del departamento a eliminar", required = true) @PathVariable Long id
    ) {
        departamentoService.eliminar(id);
    }


}
