package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.ProyectoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Empleado;
import com.example.sistemaGestionEmpleados.models.Proyecto;
import com.example.sistemaGestionEmpleados.repositories.EmpleadoRepository;
import com.example.sistemaGestionEmpleados.repositories.ProyectoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProyectoServiceIntegrationTest {
    @Autowired
    private ProyectoService proyectoService;

    // Se necesita para crear empleados de prueba
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    // Limpia el repositorio antes de cada test
    @BeforeEach
    void setUp() {
        proyectoRepository.deleteAll();
    }

    @Test
    void cuandoGuardarProyecto_entoncesSePersisteCorrectamente() {

        Proyecto proyecto = crearProyectoDePrueba("Proyecto Phoenix", LocalDate.now(), LocalDate.now().plusMonths(6));

        Proyecto proyectoGuardado = proyectoService.guardar(proyecto);

        assertNotNull(proyectoGuardado);
        assertNotNull(proyectoGuardado.getId());
        assertEquals("Proyecto Phoenix", proyectoGuardado.getNombre());
        assertTrue(proyectoRepository.existsById(proyectoGuardado.getId()));
    }

    @Test
    void cuandoBuscarPorIdExistente_entoncesRetornaProyecto() {

        Proyecto proyectoGuardado = proyectoService.guardar(crearProyectoDePrueba("Proyecto Titán", LocalDate.now(), LocalDate.now().plusYears(1)));

        Proyecto encontrado = proyectoService.buscarPorId(proyectoGuardado.getId());

        assertNotNull(encontrado);
        assertEquals(proyectoGuardado.getId(), encontrado.getId());
        assertEquals("Proyecto Titán", encontrado.getNombre());
    }

    @Test
    void cuandoBuscarPorIdNoExistente_entoncesLanzaProyectoNoEncontradoException() {

        Long idInexistente = 404L;

        ProyectoNoEncontradoException exception = assertThrows(ProyectoNoEncontradoException.class, () -> {
            proyectoService.buscarPorId(idInexistente);
        });

        assertEquals("Proyecto no encontrado con ID: " + idInexistente, exception.getMessage());
    }

    @Test
    void cuandoActualizarProyecto_entoncesSeModificaCorrectamente() {

        Proyecto proyectoOriginal = proyectoService.guardar(crearProyectoDePrueba("Proyecto Hydra", LocalDate.now(), LocalDate.now().plusDays(10)));
        Long idOriginal = proyectoOriginal.getId();

        Proyecto datosNuevos = new Proyecto();
        datosNuevos.setNombre("Proyecto Hydra V2");
        datosNuevos.setDescripcion("Versión mejorada del proyecto Hydra");
        datosNuevos.setFechaInicio(LocalDate.now().plusDays(1));
        datosNuevos.setFechaFin(LocalDate.now().plusDays(20));

        Proyecto proyectoActualizado = proyectoService.actualizar(idOriginal, datosNuevos);

        assertNotNull(proyectoActualizado);
        assertEquals(idOriginal, proyectoActualizado.getId());
        assertEquals("Proyecto Hydra V2", proyectoActualizado.getNombre());
        assertEquals(LocalDate.now().plusDays(20), proyectoActualizado.getFechaFin());
    }

    @Test
    void cuandoActualizarProyectoNoExistente_entoncesLanzaProyectoNoEncontradoException() {

        Long idInexistente = 404L;
        Proyecto datosNuevos = crearProyectoDePrueba("Proyecto Fantasma", LocalDate.now(), LocalDate.now());

        assertThrows(ProyectoNoEncontradoException.class, () -> {
            proyectoService.actualizar(idInexistente, datosNuevos);
        });
    }

    @Test
    void cuandoEliminarProyectoExistente_entoncesDejaDeExistir() {

        Proyecto proyectoGuardado = proyectoService.guardar(crearProyectoDePrueba("Proyecto Apolo", LocalDate.now(), LocalDate.now()));
        Long id = proyectoGuardado.getId();
        assertTrue(proyectoRepository.existsById(id));

        proyectoService.eliminar(id);

        assertFalse(proyectoRepository.existsById(id));
    }

    @Test
    void cuandoEliminarProyectoNoExistente_entoncesLanzaProyectoNoEncontradoException() {

        Long idInexistente = 404L;

        assertThrows(ProyectoNoEncontradoException.class, () -> {
            proyectoService.eliminar(idInexistente);
        });
    }

    @Test
    void cuandoBuscarProyectosActivos_entoncesRetornaSoloLosNoFinalizados() {
        // Proyecto activo (fecha fin en el futuro)
        proyectoService.guardar(crearProyectoDePrueba("Proyecto Activo 1", LocalDate.now(), LocalDate.now().plusDays(1)));
        // Proyecto activo (fecha fin en el futuro lejano)
        proyectoService.guardar(crearProyectoDePrueba("Proyecto Activo 2", LocalDate.now(), LocalDate.now().plusYears(1)));
        // Proyecto finalizado (fecha fin en el pasado)
        proyectoService.guardar(crearProyectoDePrueba("Proyecto Finalizado", LocalDate.now().minusMonths(1), LocalDate.now().minusDays(1)));
        // Proyecto que finaliza hoy (no debería ser considerado activo por la consulta "After")
        proyectoService.guardar(crearProyectoDePrueba("Proyecto Finaliza Hoy", LocalDate.now().minusDays(5), LocalDate.now()));

        List<Proyecto> proyectosActivos = proyectoService.buscarPorProyectosActivos();

        assertNotNull(proyectosActivos);
        assertEquals(2, proyectosActivos.size());
        // Verificamos que los proyectos retornados son efectivamente los activos
        assertTrue(proyectosActivos.stream().allMatch(p -> p.getNombre().startsWith("Proyecto Activo")));
    }


    @Test
    void cuandoAsignarNuevosEmpleados_entoncesSeAsignanCorrectamente() {

        Proyecto proyecto = proyectoService.guardar(crearProyectoDePrueba("Proyecto Conquista", LocalDate.now(), LocalDate.now().plusYears(1)));
        Empleado empleado1 = empleadoRepository.save(crearEmpleadoDePrueba("empleado1@test.com"));
        Empleado empleado2 = empleadoRepository.save(crearEmpleadoDePrueba("empleado2@test.com"));
        Set<Long> idsEmpleados = Set.of(empleado1.getId(), empleado2.getId());

        proyectoService.asignarEmpleadosAProyecto(proyecto.getId(), idsEmpleados);

        Proyecto proyectoActualizado = proyectoRepository.findByIdWithEmpleados(proyecto.getId()).orElseThrow();
        Empleado empleado1Actualizado = empleadoRepository.findById(empleado1.getId()).orElseThrow();

        // 1. El proyecto tiene 2 empleados
        assertEquals(2, proyectoActualizado.getEmpleados().size());

        // 2. Los IDs de los empleados en el proyecto son los correctos
        Set<Long> idsEnProyecto = proyectoActualizado.getEmpleados().stream()
                .map(Empleado::getId)
                .collect(Collectors.toSet());
        assertTrue(idsEnProyecto.containsAll(idsEmpleados));

        // 3. El empleado 1 tiene el proyecto en su lista de proyectos
        assertTrue(empleado1Actualizado.getProyectos().stream()
                .anyMatch(p -> p.getId().equals(proyecto.getId())));
    }

    @Test
    void cuandoReemplazarEmpleados_entoncesSeActualizaLaAsignacion() {
        Proyecto proyecto = proyectoService.guardar(crearProyectoDePrueba("Proyecto Legado", LocalDate.now(), LocalDate.now().plusYears(1)));
        Empleado empleado1 = empleadoRepository.save(crearEmpleadoDePrueba("empleado1@test.com"));
        Empleado empleado2 = empleadoRepository.save(crearEmpleadoDePrueba("empleado2@test.com"));
        Empleado empleado3 = empleadoRepository.save(crearEmpleadoDePrueba("empleado3@test.com"));

        // Asignación inicial: empleado1 y empleado2
        proyectoService.asignarEmpleadosAProyecto(proyecto.getId(), Set.of(empleado1.getId(), empleado2.getId()));

        //Nueva asignación: empleado2 y empleado3
        proyectoService.asignarEmpleadosAProyecto(proyecto.getId(), Set.of(empleado2.getId(), empleado3.getId()));

        Proyecto proyectoActualizado = proyectoRepository.findByIdWithEmpleados(proyecto.getId()).orElseThrow();
        Empleado empleado1Actualizado = empleadoRepository.findById(empleado1.getId()).orElseThrow();
        Empleado empleado3Actualizado = empleadoRepository.findById(empleado3.getId()).orElseThrow();

        // 1. El proyecto ahora tiene 2 empleados
        assertEquals(2, proyectoActualizado.getEmpleados().size());

        // 2. El empleado 1 ya NO está en el proyecto
        assertTrue(proyectoActualizado.getEmpleados().stream().noneMatch(e -> e.getId().equals(empleado1.getId())));
        assertTrue(empleado1Actualizado.getProyectos().isEmpty(), "El empleado 1 no debería tener proyectos asignados");

        // 3. El empleado 3 AHORA SÍ está en el proyecto
        assertTrue(proyectoActualizado.getEmpleados().stream().anyMatch(e -> e.getId().equals(empleado3.getId())));
        assertTrue(empleado3Actualizado.getProyectos().stream().anyMatch(p -> p.getId().equals(proyecto.getId())));
    }

    @Test
    void cuandoQuitarTodosLosEmpleados_entoncesProyectoQuedaVacio() {
        Proyecto proyecto = proyectoService.guardar(crearProyectoDePrueba("Proyecto Solitario", LocalDate.now(), LocalDate.now().plusYears(1)));
        Empleado empleado = empleadoRepository.save(crearEmpleadoDePrueba("empleado.unico@test.com"));
        proyectoService.asignarEmpleadosAProyecto(proyecto.getId(), Set.of(empleado.getId()));

        // Act: Llamamos al método con una lista de IDs vacía
        proyectoService.asignarEmpleadosAProyecto(proyecto.getId(), Set.of());

        Proyecto proyectoActualizado = proyectoRepository.findByIdWithEmpleados(proyecto.getId()).orElseThrow();
        Empleado empleadoActualizado = empleadoRepository.findById(empleado.getId()).orElseThrow();

        // 1. La lista de empleados del proyecto está vacía
        assertTrue(proyectoActualizado.getEmpleados().isEmpty());

        // 2. La lista de proyectos del empleado está vacía
        assertTrue(empleadoActualizado.getProyectos().isEmpty());
    }


    // --- Métodos de Ayuda ---

    private Proyecto crearProyectoDePrueba(String nombre, LocalDate inicio, LocalDate fin) {
        Proyecto proy = new Proyecto();
        proy.setNombre(nombre);
        proy.setDescripcion("Descripción de prueba para el " + nombre);
        proy.setFechaInicio(inicio);
        proy.setFechaFin(fin);
        return proy;
    }

    private Empleado crearEmpleadoDePrueba(String email) {
        Empleado empleado = new Empleado();
        empleado.setNombre("EmpleadoDePrueba");
        empleado.setApellido("ApellidoPrueba"); // Campo añadido
        empleado.setEmail(email);
        empleado.setFechaContratacion(LocalDate.now()); // Campo añadido
        empleado.setSalario(new BigDecimal("50000.00")); // Campo añadido
        return empleado;
    }
}
