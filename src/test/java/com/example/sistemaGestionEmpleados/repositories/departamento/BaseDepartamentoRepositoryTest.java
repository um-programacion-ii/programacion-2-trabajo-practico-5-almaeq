package com.example.sistemaGestionEmpleados.repositories.departamento;

import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.repositories.DepartamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseDepartamentoRepositoryTest {
    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected DepartamentoRepository departamentoRepository;

    @Test
    void cuandoGuardarYBuscar_entoncesRetornaDepartamentoCorrecto() {
        // Arrange
        Departamento depto = new Departamento();
        depto.setNombre("Finanzas");
        depto.setDescripcion("Departamento de contabilidad y finanzas");
        // Persistimos el objeto y lo sincronizamos con la BD de prueba
        entityManager.persistAndFlush(depto);

        // Act
        // Buscamos el departamento por su ID
        Optional<Departamento> encontrado = departamentoRepository.findById(depto.getId());

        // Assert
        assertTrue(encontrado.isPresent(), "El departamento debería ser encontrado en la base de datos");
        assertEquals("Finanzas", encontrado.get().getNombre());
    }
}
