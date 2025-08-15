package com.example.sistemaGestionEmpleados.repositories;

import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.models.Empleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("h2") // Se asegura de usar la configuración para H2 de tu application.yml
public class EmpleadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    private Departamento departamentoPrueba;

    @BeforeEach
    void setUp() {
        // Prepara un departamento de prueba antes de cada test
        Departamento depto = new Departamento();
        depto.setNombre("Tecnología");
        departamentoPrueba = entityManager.persistAndFlush(depto);
    }

    @Test
    void cuandoFindByEmail_entoncesRetornaEmpleado() {

        entityManager.persist(crearEmpleadoDePrueba("test@example.com", new BigDecimal("50000")));

        Optional<Empleado> encontrado = empleadoRepository.findByEmail("test@example.com");

        assertTrue(encontrado.isPresent());
        assertEquals("test@example.com", encontrado.get().getEmail());
    }

    @Test
    void cuandoFindByNombreDepartamento_entoncesRetornaEmpleadosCorrectos() {

        entityManager.persist(crearEmpleadoDePrueba("dev1@example.com", new BigDecimal("60000")));
        entityManager.persist(crearEmpleadoDePrueba("dev2@example.com", new BigDecimal("70000")));

        List<Empleado> empleados = empleadoRepository.findByNombreDepartamento("Tecnología");

        assertEquals(2, empleados.size());
    }

    @Test
    void cuandoFindBySalarioBetween_entoncesRetornaEmpleadosEnRango() {

        entityManager.persist(crearEmpleadoDePrueba("salario1@example.com", new BigDecimal("50000"))); // En rango
        entityManager.persist(crearEmpleadoDePrueba("salario2@example.com", new BigDecimal("65000"))); // En rango
        entityManager.persist(crearEmpleadoDePrueba("salario3@example.com", new BigDecimal("49999.99"))); // Fuera

        List<Empleado> enRango = empleadoRepository.findBySalarioBetween(new BigDecimal("50000"), new BigDecimal("70000"));

        assertEquals(2, enRango.size());
    }

    @Test
    void cuandoFindAverageSalarioByDepartamento_entoncesCalculaPromedioCorrecto() {

        entityManager.persist(crearEmpleadoDePrueba("prom1@example.com", new BigDecimal("80000.00")));
        entityManager.persist(crearEmpleadoDePrueba("prom2@example.com", new BigDecimal("100000.00")));

        Optional<BigDecimal> promedio = empleadoRepository.findAverageSalarioByDepartamento(departamentoPrueba.getId());

        assertTrue(promedio.isPresent());
        assertEquals(0, promedio.get().compareTo(new BigDecimal("90000.00"))); // Compara BigDecimals
    }

    // --- Método de Ayuda ---
    private Empleado crearEmpleadoDePrueba(String email, BigDecimal salario) {
        Empleado empleado = new Empleado();
        empleado.setNombre("Test");
        empleado.setApellido("User");
        empleado.setEmail(email);
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(salario);
        empleado.setDepartamento(departamentoPrueba);
        return empleado;
    }
}