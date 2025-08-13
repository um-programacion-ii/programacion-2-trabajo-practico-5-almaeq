package com.example.sistemaGestionEmpleados.controllers;

import com.example.sistemaGestionEmpleados.exceptions.EmpleadoNoEncontradoException;
import com.example.sistemaGestionEmpleados.models.Departamento;
import com.example.sistemaGestionEmpleados.models.Empleado;
import com.example.sistemaGestionEmpleados.services.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(controllers = EmpleadoController.class)
public class EmpleadoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpleadoService empleadoService;

    private Empleado empleadoPrueba;
    private Departamento departamentoPrueba;

    @BeforeEach
    void setUp() {
        departamentoPrueba = new Departamento(1L, "IT", "Tecnología", Collections.emptyList());
        empleadoPrueba = new Empleado(1L, "Ana", "Gomez", "ana.gomez@test.com",
                LocalDate.now(), new BigDecimal("60000.00"), departamentoPrueba, Collections.emptySet());
    }

    @Test
    void cuandoObtenerTodos_entoncesRetornaListaDeEmpleados() throws Exception {

        given(empleadoService.obtenerTodos()).willReturn(List.of(empleadoPrueba));

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Ana")));
    }

    @Test
    void cuandoObtenerPorIdExistente_entoncesRetornaEmpleado() throws Exception {
        // Arrange
        given(empleadoService.buscarPorId(1L)).willReturn(empleadoPrueba);

        mockMvc.perform(get("/api/empleados/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Ana")))
                .andExpect(jsonPath("$.email", is("ana.gomez@test.com")));
    }

    @Test
    void cuandoObtenerPorIdNoExistente_entoncesRetornaNotFound() throws Exception {

        given(empleadoService.buscarPorId(99L)).willThrow(new EmpleadoNoEncontradoException("Empleado no encontrado"));

        mockMvc.perform(get("/api/empleados/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoCrearEmpleado_entoncesRetornaCreated() throws Exception {

        given(empleadoService.guardar(any(Empleado.class))).willReturn(empleadoPrueba);

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoPrueba)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Ana")));
    }

    @Test
    void cuandoActualizarEmpleado_entoncesRetornaOk() throws Exception {

        Empleado empleadoActualizado = new Empleado(1L, "Ana", "Gomez Actualizado", "ana.gomez@test.com",
                LocalDate.now(), new BigDecimal("65000.00"), departamentoPrueba, Collections.emptySet());

        given(empleadoService.actualizar(anyLong(), any(Empleado.class))).willReturn(empleadoActualizado);

        mockMvc.perform(put("/api/empleados/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido", is("Gomez Actualizado")))
                .andExpect(jsonPath("$.salario", is(65000.00)));
    }

    @Test
    void cuandoActualizarEmpleadoNoExistente_entoncesRetornaNotFound() throws Exception {

        given(empleadoService.actualizar(anyLong(), any(Empleado.class)))
                .willThrow(new EmpleadoNoEncontradoException("Empleado no encontrado"));

        mockMvc.perform(put("/api/empleados/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoPrueba)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoEliminarEmpleado_entoncesRetornaNoContent() throws Exception {

        // doNothing es para métodos void. Le decimos a Mockito que no haga nada cuando se llame a eliminar(1L).
        doNothing().when(empleadoService).eliminar(1L);

        mockMvc.perform(delete("/api/empleados/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
