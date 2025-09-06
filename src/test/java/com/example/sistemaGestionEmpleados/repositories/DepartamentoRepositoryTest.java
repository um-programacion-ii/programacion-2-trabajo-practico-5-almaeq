package com.example.sistemaGestionEmpleados.repositories;

import com.example.sistemaGestionEmpleados.models.Departamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("h2")
public class DepartamentoRepositoryTest {
    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected DepartamentoRepository departamentoRepository;

    @Test
    void cuandoGuardarYBuscar_entoncesRetornaDepartamentoCorrecto() {

        Departamento depto = new Departamento();
        depto.setNombre("Ventas");
        entityManager.persistAndFlush(depto);

        Optional<Departamento> encontrado = departamentoRepository.findById(depto.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Ventas", encontrado.get().getNombre());
    }
}
