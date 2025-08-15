package com.example.sistemaGestionEmpleados.repositories;

import com.example.sistemaGestionEmpleados.models.Proyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private Proyecto crearProyecto(String nombre, LocalDate fechaFin) {
        Proyecto proy = new Proyecto();
        proy.setNombre(nombre);
        proy.setFechaInicio(LocalDate.now().minusDays(30));
        proy.setFechaFin(fechaFin);
        return proy;
    }
}