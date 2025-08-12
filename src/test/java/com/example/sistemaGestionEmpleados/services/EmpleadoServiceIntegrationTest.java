package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.EmailDuplicadoException;
import com.example.sistemaGestionEmpleados.exceptions.EmpleadoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.models.Empleado;
import com.example.sistemaGestionEmpleados.repositories.DepartamentoRepository;
import com.example.sistemaGestionEmpleados.repositories.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class EmpleadoServiceIntegrationTest {
    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private Departamento departamentoPrueba;

    // Preparar datos
    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();

        Departamento depto = new Departamento();
        depto.setNombre("Tecnología");
        depto.setDescripcion("Departamento de desarrollo y soporte");
        departamentoPrueba = departamentoRepository.save(depto);
    }

    @Test
    void cuandoGuardarEmpleado_entoncesSePersisteCorrectamente() {

        Empleado empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan.perez@empresa.com");
        empleado.setFechaContratacion(LocalDate.now());
        empleado.setSalario(new BigDecimal("50000.00"));
        empleado.setDepartamento(departamentoPrueba);

        Empleado empleadoGuardado = empleadoService.guardar(empleado);

        assertNotNull(empleadoGuardado);
        assertNotNull(empleadoGuardado.getId());
        assertEquals("juan.perez@empresa.com", empleadoGuardado.getEmail());
        assertTrue(empleadoRepository.existsById(empleadoGuardado.getId()));
    }

    @Test
    void cuandoGuardarEmpleadoConEmailExistente_entoncesLanzaEmailDuplicadoException() {

        empleadoService.guardar(crearEmpleadoDePrueba("original@empresa.com")); // Guardamos uno primero

        Empleado empleadoDuplicado = new Empleado();
        empleadoDuplicado.setNombre("Ana");
        empleadoDuplicado.setApellido("García");
        empleadoDuplicado.setEmail("original@empresa.com"); // Email duplicado
        empleadoDuplicado.setFechaContratacion(LocalDate.now());
        empleadoDuplicado.setSalario(new BigDecimal("60000.00"));
        empleadoDuplicado.setDepartamento(departamentoPrueba);

        EmailDuplicadoException exception = assertThrows(EmailDuplicadoException.class, () -> {
            empleadoService.guardar(empleadoDuplicado);
        });

        assertEquals("El email ya está registrado: original@empresa.com", exception.getMessage());
    }


    @Test
    void cuandoBuscarPorIdExistente_entoncesRetornaEmpleado() {

        Empleado empleadoGuardado = empleadoService.guardar(crearEmpleadoDePrueba("test@empresa.com"));

        Empleado encontrado = empleadoService.buscarPorId(empleadoGuardado.getId());

        assertNotNull(encontrado);
        assertEquals(empleadoGuardado.getId(), encontrado.getId());
        assertEquals("test@empresa.com", encontrado.getEmail());
    }

    @Test
    void cuandoBuscarPorIdNoExistente_entoncesLanzaEmpleadoNoEncontradoException() {

        Long idInexistente = 999L;

        assertThrows(EmpleadoNoEncontradoException.class, () -> {
            empleadoService.buscarPorId(idInexistente);
        });
    }

    @Test
    void cuandoActualizarEmpleado_entoncesSeModificaCorrectamente() {

        Empleado empleadoOriginal = empleadoService.guardar(crearEmpleadoDePrueba("update@empresa.com"));
        Long idOriginal = empleadoOriginal.getId();

        Empleado datosNuevos = new Empleado();
        datosNuevos.setNombre("Carlos");
        datosNuevos.setApellido("López");
        datosNuevos.setEmail("nuevo.email@empresa.com");
        datosNuevos.setSalario(new BigDecimal("75000.00"));

        Empleado empleadoActualizado = empleadoService.actualizar(idOriginal, datosNuevos);

        assertNotNull(empleadoActualizado);
        assertEquals(idOriginal, empleadoActualizado.getId());
        assertEquals("Carlos", empleadoActualizado.getNombre());
        assertEquals("López", empleadoActualizado.getApellido());
        assertEquals(new BigDecimal("75000.00"), empleadoActualizado.getSalario());
    }

    @Test
    void cuandoActualizarEmpleadoNoExistente_entoncesLanzaEmpleadoNoEncontradoException() {

        Long idInexistente = 999L;
        Empleado datosNuevos = crearEmpleadoDePrueba("dummy@empresa.com");

        assertThrows(EmpleadoNoEncontradoException.class, () -> {
            empleadoService.actualizar(idInexistente, datosNuevos);
        });
    }

    @Test
    void cuandoEliminarEmpleadoExistente_entoncesDejaDeExistir() {

        Empleado empleadoGuardado = empleadoService.guardar(crearEmpleadoDePrueba("delete@empresa.com"));
        Long id = empleadoGuardado.getId();
        assertTrue(empleadoRepository.existsById(id));

        empleadoService.eliminar(id);

        assertFalse(empleadoRepository.existsById(id));
    }

    @Test
    void cuandoEliminarEmpleadoNoExistente_entoncesLanzaEmpleadoNoEncontradoException() {

        Long idInexistente = 999L;

        assertThrows(EmpleadoNoEncontradoException.class, () -> {
            empleadoService.eliminar(idInexistente);
        });
    }

    @Test
    void cuandoObtenerTodos_entoncesRetornaListaDeEmpleados() {

        empleadoService.guardar(crearEmpleadoDePrueba("empleado1@empresa.com"));
        empleadoService.guardar(crearEmpleadoDePrueba("empleado2@empresa.com"));

        List<Empleado> empleados = empleadoService.obtenerTodos();

        assertNotNull(empleados);
        assertEquals(2, empleados.size());
    }


    //Método de ayuda para crear un empleado de prueba con datos por defecto.

    private Empleado crearEmpleadoDePrueba(String email) {
        Empleado empleado = new Empleado();
        empleado.setNombre("Empleado");
        empleado.setApellido("De Prueba");
        empleado.setEmail(email);
        empleado.setFechaContratacion(LocalDate.of(2023, 1, 15));
        empleado.setSalario(new BigDecimal("55000.00"));
        empleado.setDepartamento(departamentoPrueba);
        return empleado;
    }
}






