package com.example.sistemaGestionEmpleados.repositories;

import com.example.sistemaGestionEmpleados.models.Empleado;
import com.example.sistemaGestionEmpleados.models.Proyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
public class ProyectoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Test
    void cuandoFindByFechaFinAfter_entoncesRetornaProyectosActivos() {

        entityManager.persist(crearProyecto("Proyecto Activo", LocalDate.now().plusDays(1)));
        entityManager.persist(crearProyecto("Proyecto Finalizado", LocalDate.now().minusDays(1)));
        entityManager.persist(crearProyecto("Proyecto Finaliza Hoy", LocalDate.now()));

        List<Proyecto> proyectosActivos = proyectoRepository.findByFechaFinAfter(LocalDate.now());

        assertEquals(1, proyectosActivos.size());
        assertEquals("Proyecto Activo", proyectosActivos.get(0).getNombre());
    }

    @Test
    void cuandoFindByIdWithEmpleados_entoncesRetornaProyectoConEmpleadosCargados() {

        Proyecto proyecto = crearProyecto("Proyecto con Empleados", LocalDate.now().plusMonths(1));
        Empleado empleado1 = crearEmpleado("empleado1@test.com");
        Empleado empleado2 = crearEmpleado("empleado2@test.com");

        entityManager.persist(empleado1);
        entityManager.persist(empleado2);

        proyecto.getEmpleados().add(empleado1);
        proyecto.getEmpleados().add(empleado2);
        entityManager.persist(proyecto);
        entityManager.flush(); // Forzamos la sincronización con la BD

        // Act: Llamamos al método del repositorio que queremos probar
        Optional<Proyecto> proyectoEncontradoOpt = proyectoRepository.findByIdWithEmpleados(proyecto.getId());

        assertTrue(proyectoEncontradoOpt.isPresent());
        Proyecto proyectoEncontrado = proyectoEncontradoOpt.get();
        assertEquals("Proyecto con Empleados", proyectoEncontrado.getNombre());

        // La aserción más importante: verificamos que la colección de empleados se cargó
        assertFalse(proyectoEncontrado.getEmpleados().isEmpty(), "La lista de empleados no debería estar vacía");
        assertEquals(2, proyectoEncontrado.getEmpleados().size());
    }


    // --- Métodos de Ayuda ---
    private Proyecto crearProyecto(String nombre, LocalDate fechaFin) {
        Proyecto proy = new Proyecto();
        proy.setNombre(nombre);
        proy.setFechaInicio(LocalDate.now().minusDays(30));
        proy.setFechaFin(fechaFin);
        return proy;
    }

    private Empleado crearEmpleado(String email) {
        Empleado empleado = new Empleado();
        empleado.setNombre("Test");
        empleado.setApellido("Repo");
        empleado.setEmail(email);
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(new BigDecimal("1000"));
        return empleado;
    }
}