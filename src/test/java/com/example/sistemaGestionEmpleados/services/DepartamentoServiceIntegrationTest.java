package com.example.sistemaGestionEmpleados.services;

import com.example.sistemaGestionEmpleados.exceptions.DepartamentoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.repositories.DepartamentoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class DepartamentoServiceIntegrationTest {
    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    // Limpia la base de datos antes de cada prueba para asegurar el aislamiento
    @BeforeEach
    void setUp() {
        departamentoRepository.deleteAll();
    }

    @Test
    void cuandoGuardarDepartamento_entoncesSePersisteCorrectamente() {

        Departamento departamento = new Departamento();
        departamento.setNombre("Recursos Humanos");
        departamento.setDescripcion("Departamento de gestión de personal");

        Departamento departamentoGuardado = departamentoService.guardar(departamento);

        assertNotNull(departamentoGuardado);
        assertNotNull(departamentoGuardado.getId());
        assertEquals("Recursos Humanos", departamentoGuardado.getNombre());
        assertTrue(departamentoRepository.existsById(departamentoGuardado.getId()));
    }

    @Test
    void cuandoBuscarPorIdExistente_entoncesRetornaDepartamento() {

        Departamento departamentoGuardado = departamentoService.guardar(crearDepartamentoDePrueba("Finanzas"));

        Departamento encontrado = departamentoService.buscarPorId(departamentoGuardado.getId());

        assertNotNull(encontrado);
        assertEquals(departamentoGuardado.getId(), encontrado.getId());
        assertEquals("Finanzas", encontrado.getNombre());
    }

    @Test
    void cuandoBuscarPorIdNoExistente_entoncesLanzaDepartamentoNoEncontradoException() {

        Long idInexistente = 123L;

        DepartamentoNoEncontradoException exception = assertThrows(DepartamentoNoEncontradoException.class, () -> {
            departamentoService.buscarPorId(idInexistente);
        });

        assertEquals("Departamento no encontrado con ID: " + idInexistente, exception.getMessage());
    }

    @Test
    void cuandoActualizarDepartamento_entoncesSeModificaCorrectamente() {

        Departamento departamentoOriginal = departamentoService.guardar(crearDepartamentoDePrueba("Marketing"));
        Long idOriginal = departamentoOriginal.getId();

        Departamento datosNuevos = new Departamento();
        datosNuevos.setNombre("Marketing Digital");
        datosNuevos.setDescripcion("Estrategias de marketing online");

        Departamento departamentoActualizado = departamentoService.actualizar(idOriginal, datosNuevos);

        assertNotNull(departamentoActualizado);
        assertEquals(idOriginal, departamentoActualizado.getId());
        assertEquals("Marketing Digital", departamentoActualizado.getNombre());
        assertEquals("Estrategias de marketing online", departamentoActualizado.getDescripcion());
    }

    @Test
    void cuandoActualizarDepartamentoNoExistente_entoncesLanzaDepartamentoNoEncontradoException() {

        Long idInexistente = 123L;
        Departamento datosNuevos = crearDepartamentoDePrueba("Ventas");

        assertThrows(DepartamentoNoEncontradoException.class, () -> {
            departamentoService.actualizar(idInexistente, datosNuevos);
        });
    }

    @Test
    void cuandoEliminarDepartamentoExistente_entoncesDejaDeExistir() {

        Departamento departamentoGuardado = departamentoService.guardar(crearDepartamentoDePrueba("Logística"));
        Long id = departamentoGuardado.getId();
        assertTrue(departamentoRepository.existsById(id));

        departamentoService.eliminar(id);

        assertFalse(departamentoRepository.existsById(id));
    }

    @Test
    void cuandoEliminarDepartamentoNoExistente_entoncesLanzaDepartamentoNoEncontradoException() {

        Long idInexistente = 123L;

        assertThrows(DepartamentoNoEncontradoException.class, () -> {
            departamentoService.eliminar(idInexistente);
        });
    }

    @Test
    void cuandoObtenerTodos_entoncesRetornaListaDeDepartamentos() {

        departamentoService.guardar(crearDepartamentoDePrueba("Calidad"));
        departamentoService.guardar(crearDepartamentoDePrueba("Soporte"));

        List<Departamento> departamentos = departamentoService.obtenerTodos();

        assertNotNull(departamentos);
        assertEquals(2, departamentos.size());
    }

     //Método de ayuda para crear una instancia de Departamento con un nombre específico.

    private Departamento crearDepartamentoDePrueba(String nombre) {
        Departamento depto = new Departamento();
        depto.setNombre(nombre);
        depto.setDescripcion("Descripción de prueba para " + nombre);
        return depto;
    }
}
