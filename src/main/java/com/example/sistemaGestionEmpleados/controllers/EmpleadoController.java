package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.models.Empleado;
import com.example.sistemaGestionEmpleados.services.EmpleadoService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@Validated
@Tag(name = "API de Empleados", description = "Operaciones CRUD y consultas para la gestión de empleados")
public class EmpleadoController {
    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /**
     * Javadoc: Recupera una lista de todos los empleados registrados en el sistema.
     * @return Una lista de objetos Empleado.
     */
    @Operation(summary = "Obtener todos los empleados", description = "Devuelve una lista con todos los empleados.")
    @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida exitosamente")
    @GetMapping
    public List<Empleado> obtenerTodos() {
        return empleadoService.obtenerTodos();
    }

    /**
     * Javadoc: Busca un empleado específico utilizando su ID único.
     * @param id El ID del empleado a buscar.
     * @return El objeto Empleado correspondiente.
     */
    @Operation(summary = "Obtener un empleado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado",
                    content = @Content(schema = @Schema(implementation = Empleado.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public Empleado obtenerPorId(
            @Parameter(description = "ID del empleado a obtener", required = true) @PathVariable Long id
    ) {
        return empleadoService.buscarPorId(id);
    }

    /**
     * Javadoc: Registra un nuevo empleado en el sistema.
     * @param empleado El objeto Empleado a crear, enviado en el cuerpo de la petición.
     * @return El Empleado guardado con su ID asignado.
     */
    @Operation(summary = "Crear un nuevo empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "409", description = "Conflicto, el email ya existe", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado crear(@RequestBody Empleado empleado) {
        return empleadoService.guardar(empleado);
    }

    /**
     * Javadoc: Actualiza la información de un empleado existente.
     * @param id El ID del empleado a actualizar.
     * @param empleado El objeto Empleado con la nueva información.
     * @return El Empleado con sus datos actualizados.
     */
    @Operation(summary = "Actualizar un empleado existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PutMapping("/{id}")
    public Empleado actualizar(
            @Parameter(description = "ID del empleado a actualizar", required = true) @PathVariable Long id,
            @RequestBody Empleado empleado
    ) {
        return empleadoService.actualizar(id, empleado);
    }

    /**
     * Javadoc: Elimina un empleado del sistema por su ID.
     * @param id El ID del empleado a eliminar.
     */
    @Operation(summary = "Eliminar un empleado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2.6.0", description = "Empleado eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "ID del empleado a eliminar", required = true) @PathVariable Long id
    ) {
        empleadoService.eliminar(id);
    }

    /**
     * Javadoc: Busca todos los empleados que pertenecen a un departamento específico.
     * @param nombre El nombre del departamento por el cual filtrar.
     * @return Una lista de empleados que pertenecen a ese departamento.
     */
    @Operation(summary = "Buscar empleados por nombre de departamento")
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    @GetMapping("/departamento/{nombre}")
    public List<Empleado> obtenerPorDepartamento(
            @Parameter(description = "Nombre del departamento", required = true) @PathVariable String nombre
    ) {
        return empleadoService.buscarPorDepartamento(nombre);
    }

    /**
     * Javadoc: Busca empleados cuyo salario se encuentre dentro de un rango específico.
     * @param min El salario mínimo del rango.
     * @param max El salario máximo del rango.
     * @return Una lista de empleados que cumplen con el criterio de salario.
     */
    @Operation(summary = "Buscar empleados por rango de salario")
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    @GetMapping("/salario")
    public List<Empleado> obtenerPorRangoSalario(
            @Parameter(description = "Salario mínimo a buscar", required = true) @RequestParam BigDecimal min,
            @Parameter(description = "Salario máximo a buscar", required = true) @RequestParam BigDecimal max
    ) {
        return empleadoService.buscarPorRangoSalario(min, max);
    }
}
